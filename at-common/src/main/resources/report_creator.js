function expandCollapse(e) {
    var resultSection = e.target.parentNode.parentNode.parentNode;
    if (resultSection.className.indexOf("expanded") > -1) {
        resultSection.className = resultSection.className.replace("expanded-result", "collapsed-result");
    } else {
        resultSection.className = resultSection.className.replace("collapsed-result", "expanded-result");
    }
}


function expandCollapseAll(doExpand) {
    var allResultsSection = document.getElementsByClassName("test-result");
    var currentExpandStyle = (doExpand == true) ? "collapsed-result" : "expanded-result";
    var targetExpandStyle = (doExpand == true) ? "expanded-result" : "collapsed-result"
    for (var i = 0; i < allResultsSection.length; i++) {
        if (allResultsSection[i].className.indexOf((doExpand == true) ? "collapsed" : "expanded") > -1) {
            allResultsSection[i].className = allResultsSection[i].className.replace(currentExpandStyle, targetExpandStyle);
        }
    }
}

function filter_feature(failuresOnly) {
    document.getElementById((failuresOnly == true) ? "all_features_filter" : "failed_features_filter").checked = false;
    var allResultsSection = document.getElementsByClassName("test-result");
    for (var i = 0; i < allResultsSection.length; i++) {
        if (failuresOnly == true) {
            if (allResultsSection[i].getElementsByClassName("feature-description")[0].className.indexOf("failure") > -1) {
                allResultsSection[i].className = allResultsSection[i].className.replace("hidden-result", "shown-result");
            } else {
                allResultsSection[i].className = allResultsSection[i].className.replace("shown-result", "hidden-result");
            }
        } else {
            allResultsSection[i].className = allResultsSection[i].className.replace("hidden-result", "shown-result");
        }
    }
}