/**
 * Standard jQuery initialisation of the page were all buttons are assigned an
 * operation and the form doesn't really do anything.
 */
var token=undefined;
var header=undefined;
var mrn=undefined;
$(() => {
    console.log("Content Loaded");

    //Load the security parameters
    mrn = $("meta[name='_csrf']").attr("content");
//    token = $("meta[name='_csrf']").attr("content");
//    header = $("meta[name='_csrf_header']").attr("content");
//    $(document).ajaxSend(function(e, xhr, options) {
//        xhr.setRequestHeader(header, token);
//    });
});

/**
 * A helper function that displays the loader spinner.
 */
function showLoader(overlay = true) {
    if(!overlay) {
        $('#pageLoaderBackground').hide();
    } else {
        $('#pageLoaderBackground').show();
    }
    $('#pageLoader').show();
}

/**
 * A helper function that hides the loader spinner.
 */
function hideLoader() {
    $('#pageLoader').hide();
}

/**
 * A helpful trim function that returns null for empty strings.
 */
function trimToNull(str) {
    if(str && str.length>0) {
        return str.trim();
    } else {
        return null;
    }
}

/**
 * Defines a common way of dealing with the AJAX call errors, i.e if we have a
 * callback use it, otherwise just show the error in the console.
 */
function handleAjaxError(response, status, more, errorCallback) {
    if(errorCallback) {
        errorCallback(response, status, more);
    } else {
        console.error(response);
    }
}

/**
 * A helper function that shows the error boostrap error dialog and displays
 * the provided error message in it.
 */
function showError(errorMsg) {
    if(!errorMsg || errorMsg.length === 0) {
        errorMsg = "An unknown error occurred while performing this action.";
    } else {
        errorMsg = errorMsg.replace(/^(error\.)/,"");
    }
    $('#error-dialog').modal('show');
    $('#error-dialog .modal-body').html(`<p class="text-danger">${errorMsg}</p>`);
}