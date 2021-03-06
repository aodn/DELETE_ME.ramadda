
gen::setDoTclEvaluation 1
gen::setTargetDir ../htdocs/userguide


namespace eval wiki {}

proc wiki::tagdef {t {attrs {}}} {
    if {$attrs==""} {
        return "<a name=\"$t\"></a>{{$t}}"
    } else {
        return "<a name=\"$t\"></a>{{$t <i>$attrs</i>}}"
    }
}

proc wiki::tag {t {attrs {}}} {
    if {$attrs==""} {
        return "{{$t}}"
    } else {
        return "{{$t <i>$attrs</i>}}"
    }
}

proc wiki::text {t} {
    return "<blockquote><pre>$t</pre></blockquote>"
}

proc class {c} {
    return "<code>$c</code>"
}


proc import {file} {
   set fp [open $file r ]
   set c [read $fp]
   return $c
}


proc xml {args} {
  set xml [ht::pre [join $args " "]]
  regsub -all {(&lt;!--.*?--&gt;)} $xml {<span class="xmlcomment">\1</span>} xml
  regsub -all {(&lt;!\[CDATA\[.*?\]\]&gt;)} $xml {<span class="xmlcdata">\1</span>} xml
  regsub -all {([^\s]*)=&quot;} $xml {<span class="xmlattr">\1</span>="} xml


  foreach t [array names ::taghome] {
     regsub -all "lt;$t" $xml "lt;<a[tagref $t]" xml
     regsub -all "/$t" $xml "/<a[tagref $t]" xml
  }
  return "<blockquote>$xml</blockquote>"
}



proc importxml {file} {
   lappend ::filesToCopy [file join  $file] [file join [gen::getTargetDir]  [file dirname $file]]
   set xml [import  $file]
   set href "<a href=\"$file\"><img src=\"folder.gif\" border=\"0\">$file</a>"
   set xml [xml [string trim $xml]]
   regsub {</pre>\s*</blockquote>} $xml "" xml
##   append xml "\n$href</pre></blockquote>"
   set xml
}

