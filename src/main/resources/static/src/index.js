/**
 * Global Variables
 */
var drawnItems = undefined;
var stompClient = null;
var subscriptionMap = undefined;
var southWest = L.latLng(-89.98155760646617, -180);
var northEast = L.latLng(89.99346179538875, 180);
var bounds = L.latLngBounds(southWest, northEast);
var atonMarkers = [];
var subscriptionIdentifier;

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
    // Connect the clear button
    $("#clearButton").click(function() {
        clearForm();
    });

    // Connect the subscribe button
    $("#subscribeButton").click(function() {
        // Check the form configuration
        if (!$('#subscriptionForm')[0].checkValidity()) {
            $('#subscriptionForm')[0].reportValidity();
            return;
        }

        // And subscribe
        subscribe();
    });

    // Connect the unsubscribe button
    $("#unsubscribeButton").hide();
    $("#unsubscribeButton").click(function() {
        // Check the form configuration
        if (!$('#subscriptionForm')[0].checkValidity()) {
            $('#subscriptionForm')[0].reportValidity();
            return;
        }

        unsubscribe();
    });

    // Finally also initialise the search map before we need it
    if($('#subscriptionMap').length) {
        subscriptionMap = L.map('subscriptionMap', {
            //noWrap: true,
            center: bounds.getCenter(),
            zoom: 2,
            maxBounds: bounds,
            maxBoundsViscosity: 1.0
        })
        L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(subscriptionMap);

        // FeatureGroup is to store editable layers
        drawnItems = new L.FeatureGroup();
        subscriptionMap.addLayer(drawnItems);
    }

    // Connect the web-socket
    connect();

    // Hide the spinner
    $("#subscriptionInProcessSpinner").hide();
});

/**
 * This function handles the connection to the web-socket served by our
 * web-app. Basically, it connects and starts printing out the messages.
 */
function connect() {
    var endpoint = $( "#endpoint option:selected" ).text();
    if(stompClient == null) {
        var socket = new SockJS(location.href.substring(0, location.href.lastIndexOf('/')) + '/aton-service-client-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/secom/subscription/created', function (msg) {
                $("#subscriptionFail").hide();
                $("#subscriptionSuccess").show();
            });
            stompClient.subscribe('/topic/secom/subscription/removed', function (msg) {
                $("#subscriptionSuccess").hide();
                $("#subscriptionFail").show();
            });
            stompClient.subscribe('/topic/secom/subscription/update', function (msg) {
                loadAtoNGeometry(JSON.parse(msg.body));
            });
        });
    } else {
        stompClient.subscribe('/topic/secom/subscription/created', function (msg) {
            $("#subscriptionFail").hide();
            $("#subscriptionSuccess").show();
        });
        stompClient.subscribe('/topic/secom/subscription/removed', function (msg) {
            $("#subscriptionSuccess").hide();
            $("#subscriptionFail").show();
        });
        stompClient.subscribe('/topic/secom/subscription/update', function (msg) {
            loadAtoNGeometry(JSON.parse(msg.body));
        });
    }
}

/**
 * Clear out the Subscription form.
 */
function clearForm() {
    $('#subscriptionForm')[0].reset();
    clearAtonMarkers();
}

/**
 * Generates a new subscription based on the populated information in the
 * web form.
 */
function subscribe() {
    // Get the AtoN Service URL to subscribe to
    var atonServiceUrl = trimToNull($("#atonServiceUrlInput").val());

    // Create the subscription request
    var subscriptionRequestObject = {
        containerType: trimToNull($("#containerTypeInput").val()),
        dataProductType: trimToNull($("#dataProductTypeInput").val()),
        dataReference: trimToNull($("#dataReferenceInput").val()),
        productVersion: trimToNull($("#productVersionInput").val()),
        geometry: trimToNull($("#geometyInput").val()),
        unlocode: trimToNull($("#unlocodeInput").val()),
        subscriptionPeriodStart: dateToSecomFormat($("#subscriptionPeriodStartInput").val()),
        subscriptionPeriodEnd: dateToSecomFormat($("#subscriptionPeriodEndInput").val())
    };

    // Perform the Subscription API request
    subscriptionApi.subscribe(atonServiceUrl, subscriptionRequestObject, (subscriptionResponse) => {
        subscriptionIdentifier = subscriptionResponse.subscriptionIdentifier;
        // Hide the subscribe button
        $("#subscribeButton").hide();
        $("#unsubscribeButton").show();
    }, (response, status, more, errorCallback) => {
        console.error(response);
        showError(response.statusText);
    });
}

/**
 * Removes an existing subscription based on the populated information in the
 * web form.
 */
function unsubscribe() {
    // Get the AtoN Service URL to subscribe to
    var atonServiceUrl = trimToNull($("#atonServiceUrlInput").val());

    // Create the remove subscription request
    var removeSubscriptionObject = {
        subscriptionIdentifier: trimToNull(subscriptionIdentifier)
    };

    // Perform the Subscription API request
    subscriptionApi.unsubscribe(atonServiceUrl, removeSubscriptionObject, (subscriptionResponse) => {
       subscriptionIdentifier = undefined;
       // Hide the unsubscribe button
       $("#unsubscribeButton").hide();
       $("#subscribeButton").show();
    }, (response, status, more, errorCallback) => {
        console.error(response);
        showError(response.statusText);
    });
}

/**
 * This function will load the AtoN geometry onto the drawnItems variable
 * so that it is shown in the station maps layers.
 *
 * @param {Object}        aton          The AtoN object to be drawn on the map
 */
function loadAtoNGeometry(aton) {
    atonMarker = L.marker([
                aton.geometry.pointProperty.point.pos.values[1],
                aton.geometry.pointProperty.point.pos.values[0]
            ])
            .addTo(subscriptionMap)
            .bindPopup(aton.atonNumber + ' - ' + aton.textualDescription);

    // And add the new marker in the satellite position markers
    atonMarkers.push(atonMarker);
}

/**
 * Clears all the satellite map positions from the RAIM Availability map.
 */
function clearAtonMarkers() {
    // First check that we have a map
    if(subscriptionMap == undefined) {
        return;
    }

    // Clear the markers that have already been added
    if (atonMarkers != undefined) {
        for(var i=atonMarkers.length-1; i >= 0; i--) {
            subscriptionMap.removeLayer(atonMarkers[i]);
            atonMarkers.splice(i, 1);
        }
    };
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
            .split(.)[0] + "Z";
}
