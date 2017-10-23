package com.fortegrp.at.common.report.internal

class ReportStylesheet {


    static def styleMap = [
            "body"                     : "font-family: Helvetica; ",
            "table"                    : "margin: 5px; ",
            "div.date-test-ran"        : "font-size: small;font-style: italic; ",
            "div.execution-start-time" : "font-size: small;  font-style: italic; ",
            "table.summary-table"      : "width: 800px;text-align: left; font-weight: bold; font-size: small; margin: 5px; ",
            "table.summary-table th"   : "background: lightblue; padding: 6px; ",
            "table.summary-table td"   : " background: #E0E0E0; padding: 3px; ",
            "tr.error td, td.error"    : "background-color: #F89A4F; ",
            "tr.failure td, td.failure": "color: red; ",
            "div.footer"               : "text-align: center;  font-size: small; ",
            ".artificial_hide"         : "opacity: 0; ",
            "td.last"                  : "white-space: nowrap; ",
            ".ignored"                  : "color: gray; "]

}
