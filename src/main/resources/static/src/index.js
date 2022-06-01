/**
 * Global Variables
 */
var subscriptionMap = undefined;
var southWest = L.latLng(-89.98155760646617, -180);
var northEast = L.latLng(89.99346179538875, 180);
var bounds = L.latLngBounds(southWest, northEast);

// Define an icon for the satellite markers
var satelliteIcon = L.icon({
    iconUrl: 'images/satellite-icon.png',

    iconSize:     [26, 25], // size of the icon
    iconAnchor:   [13, 12], // point of the icon which will correspond to marker's location
    popupAnchor:  [0, 0]    // point from which the popup should open relative to the iconAnchor
});

// Define an icon for the available satellite markers
var satelliteIconAvail = L.icon({
    iconUrl: 'images/satellite-icon-green.png',

    iconSize:     [26, 25], // size of the icon
    iconAnchor:   [13, 12], // point of the icon which will correspond to marker's location
    popupAnchor:  [0, 0]    // point from which the popup should open relative to the iconAnchor
});

/**
 * API Libraries
 */
subscriptionApi = new SubscriptionApi();

/**
 * The jquery document ready function.
 */
$(() => {
    // Connect the RAIM Availability Clear button
    $("#unsubscribeButton").click(function() {
        clearForm();
    });

    // Connect the RAIM Availability Check button
    $("#subscribeButton").click(function() {
        // Check the form configuration
        if (!$('#subscriptionForm')[0].checkValidity()) {
            $('#subscriptionForm')[0].reportValidity();
            return;
        }

        // And subscribe
        subscribe();
    });

    // Finally also initialise the search map before we need it
    if($('#subscriptionMap').length) {
        raimMap = L.map('subscriptionMap', {
            //noWrap: true,
            center: bounds.getCenter(),
            zoom: 2,
            maxBounds: bounds,
            maxBoundsViscosity: 1.0
        })
        L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(raimMap);
    }

    // Hide the spinner
    $("#subscriptionInProcessSpinner").hide();
});

/**
 * Clear out the RAIM availability form.
 */
function clearForm() {
    $('#subscriptionForm')[0].reset();
}

/**
 * Generates a new subscription based on the populated information in the
 * web form.
 */
function subscribe() {

    // Create the raim request
    var subscriptionRequest = {
        containerType: trimToNull($("#containerTypeInput").val()),
        dataProductType: trimToNull($("#dataProductTypeInput").val()),
        dataReference: trimToNull($("#dataReferenceInput").val()),
        productVersion: trimToNull($("#productVersionInput").val()),
        geometry: trimToNull($("#geometyInput").val()),
        unlocode: trimToNull($("#unlocodeInput").val()),
        subscriptionPeriodStart: trimToNull($("#subscriptionPeriodStartInput").val()),
        subscriptionPeriodEnd: trimToNull($("#subscriptionPeriodEndInput").val())
    };

    // Perform the Subscription API request
    subscriptionApi.subscribe(subscriptionRequest, (subscriptionResponse) => {
       console.log(subscriptionResponse);
    }, (response, status, more, errorCallback) => {
        console.error(response);
        showError(response.statusText);
    });
}
