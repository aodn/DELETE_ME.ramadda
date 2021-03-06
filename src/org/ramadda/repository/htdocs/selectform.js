
function SelectForm (formId, entryId, arg, outputDiv, selectValues) {
    this.id = formId;
    this.entryId = entryId;
    this.arg = arg;
    this.outputDivPrefix = outputDiv;
    this.selectValues = selectValues;
    this.totalSize = 0;
    this.checkboxPrefix = "entry_" + this.id+"_";
    if(!this.arg) this.arg = "select";

    this.clearSelect = function (num) {
        for(var i=num;i<10;i++) {
            select = this.getSelect(i);
            if(select.size()==0) break;
            select.html("<select><option value=''>--</option></select>");
        }
    }



    this.valueDefined =function(value) {
        if(value != "" && value.indexOf("--") != 0) {
            return true;
        }
        return false;
    }



    this.getUrl = function(what) {
        var url = "${urlroot}/entry/show?entryid=" + this.entryId;
        var theForm = this;
        var inputs = $('#' + this.id +' ::input');
        //        $(':input[id*=\"' + this.id +'\"]')
        inputs.each(function() {             
                //if(this.name == "entryselect" && !this.attr('checked')) {
                var value = $(this).val();
                if(this.name == "entryselect") {
                    if(!$(this).is(':checked')) {
                        return;
                    }
                }
                //A hack for now but 
                if(this.type == 'radio') {
                    if(!$(this).is(':checked')) {
                        return;
                    }
                }

                if(theForm.valueDefined(value)) {
                     url += "&" + this.name+ "=" + encodeURIComponent(value);
                }
         });       
        if(what!=null) {
            url += "&request=" + what;
        }
        return url;
    }

    this.download = function(event) {
        var url = this.getUrl("download");
        event.preventDefault();
        window.location.href = url;
    }

    this.makeKMZ = function(event) {
        var url = this.getUrl("kmz");
        event.preventDefault();
        window.location.href = url;
    }



    this.search = function(event) {
        var result = "";
        var url = this.getUrl("search");
        var theForm = this;
        $("#" + this.outputDivPrefix+"list").html("<img src=" + icon_progress +"> Searching...");
        $("#" + this.outputDivPrefix+"image").html("");
        theForm.totalSize = 0;
        $.getJSON(url, function(data) {
                theForm.processEntryJson(data);
            });

        return false;
    }


    this.makeImage = function(event) {
        var result = "";
        var url = this.getUrl("image");
        var theForm = this;
        //        $("#" + this.outputDivPrefix+"image").html("<img src=" + icon_progress +"> Creating image");
        $("#" + this.outputDivPrefix+"image").html("<img alt=\"Generating Image....\" src=\"" + url+"\">");
        //        theForm.totalSize = 0;
        return false;
    }



    this.processEntryJson = function(data) {
        var totalSize =0;
        var html = "";
        if(data.length==0) {
            html = "Nothing found";
        } else {
            var firstColWidth = "40%";
            var widthPerColumn=0;
            var listHtml = "";
            var header = "";
            var footer= "";
            var columnNames = null;
            var row1 = true;
            for(var i=0;i<data.length;i++)  {
                var entry = new Entry(data[i]);
                if(i==0) {
                    columnNames = entry.getColumnNames();
                    widthPerColumn = Math.floor(60/(columnNames.length+1))+"%";
                    var labels = entry.getColumnLabels();
                    for(var colIdx=0;colIdx<labels.length;colIdx++) {
                        header+="<td width=" + widthPerColumn +"><b>" + labels[colIdx] +"</b></td>";
                        footer+="<td></td>";
                    }
                }

                if(row1)
                    listHtml += "<tr class=listrow1>";
                else
                    listHtml += "<tr class=listrow2>";
                row1 = !row1;
                listHtml+= "<td width=" + firstColWidth+" ><input name=\"entryselect\" type=checkbox checked value=\"" + entry.getId() +"\" id=\"" +
                    this.checkboxPrefix + 
                    + entry.getId() +"\" >";
                listHtml+= entry.getLink(entry.getIconImage()  + " " + entry.getName());

                for(var colIdx=0;colIdx<columnNames.length;colIdx++) {
                    var value = entry.getColumnValue(columnNames[colIdx]);
                    listHtml+= "<td width=" + widthPerColumn +">" + value +"</td>";
                }

                listHtml += "</td><td align=right width=10%>";
                listHtml+= entry.getFormattedFilesize();

                totalSize += entry.getFilesize();
                listHtml += "</tr>";
            }



            var tableHeader = "<table width=100% cellpadding=3 cellspacing=0 id=\"listing\">";

            html += "<div>";
            html += tableHeader;
            html += "<thead><tr style=\"background: #FFF;\">"; 
            html+= "<td width=" + firstColWidth +">";
            var checkboxId = this.id +"_listcbx";
            html+= "<input type=checkbox checked value=true id=\"" + checkboxId  +"\"\> ";
            html += "<b>" + data.length +" files found</b></td>" + header +"<td width=" + widthPerColumn  +" align=right><b>Size</b></td></tr></thead>";
            html += "</table>"
            html += "</div>";

             
            html += "<div style=\" margin-bottom:2px;  margin-top:2px; max-height: 250px; overflow-y: auto; border: 1px #ccc solid;\">";
            html += tableHeader;
            html += listHtml;
            html += "</table>";
            html += "</div>";

            html += "<div>";
            html += tableHeader;
            html += "<thead><tr style=\"background: #Fff;\">"; 
            html += "<td width=" + firstColWidth +">";
            html += "</td>" + header +"<td width=" + widthPerColumn  +" align=right><b>" + size_format(totalSize) +"</b></td></tr></thead>";
            html += "</table>"
            html +="</div>"

        }
        this.totalSize = totalSize;
        $("#" + this.outputDivPrefix+"list").html(html);
        var theForm = this;
        var cbx  =  $("#" + checkboxId);

        this.getEntryCheckboxes().change(function(event) {
                theForm.listUpdated();
            });


        cbx.change(function(event) {
                var value = cbx.is(':checked');
                theForm.getEntryCheckboxes().attr("checked", value);
                theForm.listUpdated();
            });
        this.listUpdated()
    }



    this.getEntryCheckboxes = function () {
        return   $(':input[id*=\"' + this.checkboxPrefix +'\"]');
    }


    this.listUpdated = function () {
        var cbxs  = this.getEntryCheckboxes();
        var hasSelectedEntries = false;
        cbxs.each(function( index ) {
                if ($(this).attr("checked")) {
                    hasSelectedEntries = true;
                }
            });

        //        this.totalSize
        var btns  =  $(':input[id*=\"' + this.id +'_do_\"]');
        if (hasSelectedEntries) {
            btns.removeAttr('disabled').removeClass( 'ui-state-disabled' );
        } else {
            btns.attr('disabled', 'disabled' ).addClass( 'ui-state-disabled' );
        }
    }

    this.isSelectLinked = function () {
        return false;
    }

    this.select = function (num) {
        if (!this.isSelectLinked()) {
            return;
        }

        this.narrowSelect();
        return;
        num = parseInt(num);
        select = this.getSelect(num);
        if(select.val() == "" || select.val().indexOf("--") == 0) {
            this.clearSelect(num+1);
            return false;
        }

        var nextIdx = num+1;
        var url = this.getUrl("metadata");
        this.applyToSelect(url, nextIdx);
        return false;
    }


    this.narrowSelect = function() {
        var args="";
        for(var i=0;i<10;i++) {
            select = this.getSelect(i);
            if(select.size()==0) break;
            var value = select.val();
            if(this.valueDefined(value)) {
                args+="&" + this.arg +i + "=" + encodeURIComponent(value);
            } 
        }

        var url = this.getUrl("metadata");
        for(var i=0;i<10;i++) {
            select = this.getSelect(i);
            if(select.size()==0) break;
            var value = select.val();
            if(!this.valueDefined(value)) {
                this.applyToSelect(url+"&field=" + this.arg+i, i);
            }
        }
  }



    this.applyToSelect = function(url, index) {
        var theForm = this;
        $.getJSON(url, function(data) {
                if(!data.values) {
                    alert('error');
                    return;
                }
                var html  = "<select>";
                for (i = 0; i < data.values.length; i++) {
                    var value = data.values[i];
                    var label = value;
                    var colonIdx = value.indexOf(":");
                    if(colonIdx>=0) {
                        label = value.substring(colonIdx+1);
                        value = value.substring(0,colonIdx);
                        //                        alert("label:" + label +" value:"  + value); 
                    }
                    if(value.indexOf("--") ==0) {
                        value = "";
                    }
                    html += "<option value=\'"  + value+"\'>" + label +"</option>";
                }
                html+= "</select>";
                var nextSelect = theForm.getSelect(index);
                var currentValue = nextSelect.val();
                nextSelect.html(html);
                nextSelect.focus();
                if(currentValue) {
                    nextSelect.val(currentValue);
                }
                theForm.clearSelect(index+1);
            });

    }

    this.getSelect = function(i) {
        return $('#' + this.id+'_' + this.arg + i);
    }

    this.submit  = function() {
        var valueField = $('#' + this.id +'_value');
        var image = $('#' + this.id +'_image');
        image.attr("src", "${urlroot}/icons/" + valueField.val());
        return false;
    }

    this.listUpdated();


}