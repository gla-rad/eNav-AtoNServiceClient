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
    mrn = $("meta[name='mrn']").attr("content");
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
 * A helper function that shows the information modal dialog and accepts a
 * table of data to be shown.
 */
function showInfoTable(dataObjects) {
    // Get the information dialog body and clear it out
    const $infoDialogBody= $('#info-dialog .modal-body');
    $infoDialogBody.empty();

    // For each of the data objects create a new table
    $.each(dataObjects, function(index, dataObject) {
        // Create a table element with 'table' and 'table-striped' classes and set the id to 'info-data-table-n'
        const $table = $('<table></table>')
            .attr('id', 'info-data-table-' + index) // Set the table id
            .addClass('table table-striped mb-2'); // Add Bootstrap table classes

        // Add the table title using the <caption> tag
        if (dataObject.id) {
            const $caption = $('<caption></caption>')
                .text(dataObject.id)
                .css('caption-side', 'top') // Ensures the caption is at the top
                .addClass('table-caption h4'); // Add class if needed
            $table.append($caption);
        }

        // Create a table body element inside the table
        const $tableBody = $('<tbody></tbody>')
        $table.append($tableBody);

        // Iterate through the object keys and create table rows
        $.each(dataObject, function(key, value) {
            // Skip IDs
            if(key == 'id') {
                return;
            }

            // Skip complex objects
            let regex = /\[object .*\]/;
            if(value != undefined && regex.test(value.toString())) {
                return;
            }

            const $row = $('<tr></tr>');
            const $keyCell = $('<td class="fw-bold"></td>').text(key);
            const $valueCell = $('<td></td>').text(value);

            // Append the cells to the row
            $row.append($keyCell).append($valueCell);

            // Append the row to the table
            $tableBody.append($row);
        });

        // Add the information to the information dialog body
        $infoDialogBody.append($table);
    });

    // Finally show the dialog
    $('#info-dialog').modal('show');
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

/**
 * We want to format the date in a format that is compatible with the
 * SECOM date-time format which looks a bit like this:
 * FORMAT:  yyyyMMddTHHmmss
 * EXAMPLE: 19850412T101530
 */
function dateToSecomFormat(date) {
    // Sanity Check
    var nullCheckedDate = trimToNull(date)
    if(nullCheckedDate == null || nullCheckedDate == undefined) {
        return null;
    }

    // To achieve out goal the easier way is to get the ISO date format
    // and remove the bits we don't like
    var isoDateTimeString = new Date(date);
    return isoDateTimeString.toISOString()
            .replaceAll("-","")
            .replaceAll(":","")
            .split(".")[0] + "Z";
}

/**
 * Formats the provided string into a title case string and return the output.
 */
function toTitleCase(str) {
    // Sanity Check
    if(!str) {
        return "";
    }

    return str.toLowerCase().split(/[\s,_]+/).map((word) => {
        return (word.charAt(0).toUpperCase() + word.slice(1));
    }).join(' ');
}