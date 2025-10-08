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
var navWarnAreas = [];
var markersByPosition = new Map();
var subscriptionIdentifier;
var baseIconApiUrl = "https://rnavlab.gla-rad.org/niord-ng"

// Define an icon for generic navigational warning parts
const navWarnIcon = L.icon({
    iconUrl: './images/NavigationalWarningFeaturePart.svg',
    iconSize: [21, 21],
});

// Define an icon for point navigational warning parts
const navWarnPointIcon = L.icon({
    iconUrl: './images/NavigationalWarningFeaturePart_point.svg',
    iconSize: [41, 27],
    iconAnchor: [20, 27],
});

// Create the map drawing style for lines
const lineStyle = {
  fillColor: "none",
  color: "#B05BB6",
  weight: 4,
  dashArray: "15, 10",
  lineCap: "butt",
};
// Create the map drawing style for sufaces
const surfaceStyle = {
  color: "#B05BB6",
  weight: 4,
  dashArray: "15, 10",
  lineCap: "butt",
};
// Create the map drawing style for the whole map
const wholeMapStyle = {
  fillColor: "none",
  color: "blue",
  weight: 3,
  dashArray: "5, 10",
};

/**
 * API Libraries
 */
navwarnParser = new NWParser();
atonParser = new AtonParser();
secomServiceApi = new SecomServiceApi();
subscriptionApi = new SubscriptionApi();

/**
 * The jquery document ready function.
 */
$(() => {
// Check for an active subscription
    var subscriptionId = $("#subscribeButton").attr("data-subscriptionId");
    if(subscriptionId) {
        $("#subscribeButton").hide();
    } else {
        $("#unsubscribeButton").hide();
    }

    // Connect the clear button
    $("#clearButton").click(function() {
        clearForm();
    });

    // Connect the get button
    $("#getButton").click(function() {
        get();
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
    $("#unsubscribeButton").click(function() {
        // Check the form configuration
        if (!$('#subscriptionForm')[0].checkValidity()) {
            $('#subscriptionForm')[0].reportValidity();
            return;
        }

        unsubscribe();
    });

    // Load the selected AtoN service dataset list
    loadDatasets();
    $('input:radio').change(function() {
       if (this.checked) {
         loadDatasets();
       };
    });

    // Finally also initialise the search map before we need it
    if($('#subscriptionMap').length) {
        subscriptionMap = L.map('subscriptionMap', {
            noWrap: true,
            center: new L.LatLng(51.94194, 1.28437), // Put Harwich as the centre of the world :)
            zoom: 6,
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
                if(msg.headers["dataProductType"]=="S124") {
                    loadNavWarnGeometry(msg.headers, msg.body);
                } else if(msg.headers["dataProductType"]=="S125") {
                    loadAtoNGeometry(msg.headers, JSON.parse(msg.body));
                }
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
            if(msg.headers["dataProductType"]=="S124") {
                loadNavWarnGeometry(msg.headers, msg.body);
            } else if(msg.headers["dataProductType"]=="S125") {
                loadAtoNGeometry(msg.headers, JSON.parse(msg.body));
            }
        });
    }
}

/**
 * Clear out the Subscription form.
 */
function clearForm() {
    $('#subscriptionForm')[0].reset();
    clearMapMarkers();
}

/**
 * Provided that the user has made some selections for retrieving the S-125
 * AtoN datasets, this function will ask the backend to use the SECOM Get
 * interface to directly retrieve the data and present it to the GUI.
 */
function get() {
    // Get the AtoN Service MRN to subscribe to
    var atonServiceMrn = $('input[name="serviceSelection"]:checked').attr('id');

    // Sanity check
    if(!atonServiceMrn) {
        return;
    }

    // Perform the Subscription API request
    var dataProductType = trimToNull($("#dataProductTypeInput option:selected").val());
    var dataReference = trimToNull($("#dataReferenceInput").val());
    var geometry = trimToNull($("#geometryInput").val());
    secomServiceApi.getSecomDatasetContent(atonServiceMrn,
                                           dataProductType,
                                           dataReference,
                                           geometry,
                                           (getResponse) => {
                                               console.info("AtoN information received")
                                           },
                                           (response, status, more, errorCallback) => {
                                               console.error(response.responseJSON);
                                               // Too many errors, don't show in the GUI
                                               //showError(response.responseJSON.message);
                                           });
}

/**
 * Generates a new subscription based on the populated information in the
 * web form.
 */
function subscribe() {
    // Get the AtoN Service MRN to subscribe to
    var atonServiceMrn = $('input[name="serviceSelection"]:checked').attr('id');

    // Sanity check
    if(!atonServiceMrn) {
        return;
    }

    // Create the subscription request
    var subscriptionRequestObject = {
        containerType: 0, //trimToNull($("#containerTypeInput").val()),
        dataProductType: trimToNull($("#dataProductTypeInput").val()),
        dataReference: trimToNull($("#dataReferenceInput").val()),
        productVersion: trimToNull($("#productVersionInput").val()),
        geometry: trimToNull($("#geometyInput").val()),
        unlocode: trimToNull($("#unlocodeInput").val()),
        subscriptionPeriodStart: dateToSecomFormat($("#subscriptionPeriodStartInput").val()),
        subscriptionPeriodEnd: dateToSecomFormat($("#subscriptionPeriodEndInput").val())
    };

    // Perform the Subscription API request
    subscriptionApi.subscribe(atonServiceMrn, subscriptionRequestObject, (subscriptionResponse) => {
        subscriptionIdentifier = subscriptionResponse.subscriptionIdentifier;
        // Hide the subscribe button
        $("#subscribeButton").attr("data-subscriptionId", subscriptionIdentifier);
        $("#subscribeButton").hide();
        $("#unsubscribeButton").show();
    }, (response, status, more, errorCallback) => {
        console.error(response.responseJSON);
        showError(response.responseJSON.message);
    });
}

/**
 * Removes an existing subscription based on the populated information in the
 * web form.
 */
function unsubscribe() {
    // Get the AtoN Service MRN to subscribe to
    var atonServiceMrn = $('input[name="serviceSelection"]:checked').attr('id');;

    // Sanity check
    if(!atonServiceMrn) {
        return;
    }

    // Create the remove subscription request
    var removeSubscriptionObject = {
        subscriptionIdentifier: trimToNull(subscriptionIdentifier)
    };

    // Perform the Subscription API request
    subscriptionApi.unsubscribe(atonServiceMrn, (subscriptionResponse) => {
       subscriptionIdentifier = undefined;
       // Hide the unsubscribe button
       $("#unsubscribeButton").hide();
       $("#subscribeButton").attr("data-subscriptionId", "");
       $("#subscribeButton").show();
    }, (response, status, more, errorCallback) => {
        console.error(response.responseJSON);
        showError(response.responseJSON.message);
    });
}

/**
 * This function will load the services based on the provided data product
 * from the selected MCP service registry so that data can be retrieved.
 */
function loadServices() {
    var dataProductType = trimToNull($("#dataProductTypeInput option:selected").data('keyword'));
    secomServiceApi.getSecomServices(dataProductType,
                                     (result) => {
                                         var serviceTable = $("#serviceTable");
                                         var options = $("#dataReferenceInput");
                                         //Clear previous entries
                                         serviceTable.find('tbody').remove();
                                         options.empty()
                                         //don't forget error handling!
                                         $.each(result, function(index, instance) {
                                             serviceTable
                                                 .append($('<tbody>')
                                                    .append($('<tr>')
                                                         .append($('<td>')
                                                             .append($('<input>')
                                                                .attr('class', 'form-check-input')
                                                                .attr('type', 'radio')
                                                                .attr('name', 'serviceSelection')
                                                                .attr('id', instance.instanceId)
                                                                .attr('aria-label', 'Checkbox for selecting this service')
                                                                .change(function() {
                                                                   if (this.checked) {
                                                                     loadDatasets();
                                                                   };
                                                                })
                                                             )
                                                         )
                                                         .append($('<td>')
                                                             .attr('style', 'word-break:break-word')
                                                             .text(instance.name)
                                                         )
                                                         .append($('<td>')
                                                             .attr('style', 'word-break:break-word')
                                                             .text(instance.instanceId)
                                                         )
                                                         .append($('<td>')
                                                             .append($('<span>')
                                                                .attr('class', 'badge bg-danger')
                                                                .text('Live')
                                                             )
                                                             .append($('<span>')
                                                                .attr('class', 'badge bg-success')
                                                                .text('Online')
                                                             )
//                                                             .append($('<span>')
//                                                                .attr('class', 'badge bg-secondary')
//                                                                .text('Offline')
//                                                             )
                                                         )
                                                     )
                                                 );
                                         });
                                     }, (response, status, more, errorCallback) => {
                                         console.error(response.responseJSON);
                                         showError(response.responseJSON.message);
                                     });
}

/**
 * This function will load the S-100 service datasets currently present in the
 * selected registered service into the data reference dropdown option list.
 */
function loadDatasets() {
    // Get the AtoN Service MRN to load the datasets from
    var atonServiceMrn = $('input[name="serviceSelection"]:checked').attr('id');;

    //Clear previous entries
    $("#dataReferenceInput").empty();

    // Sanity check
    if(!atonServiceMrn) {
        return;
    }

    // Call the AtoN Service API and load the dataset options
    var dataProductType = trimToNull($("#dataProductTypeInput option:selected").val());
    secomServiceApi.getSecomDatasets(atonServiceMrn,
                                     dataProductType,
                                     (result) => {
                                         //Append the new options
                                         var options = $("#dataReferenceInput");
                                         options.append($("<option />").val(undefined).text("All Datasets"));
                                         $.each(result, function(item) {
                                             options.append($("<option />").val(result[item].dataReference).text(result[item].info_name));
                                         });
                                     },
                                     (response, status, more, errorCallback) => {
                                        console.error(response.responseJSON);
                                        // Too many errors, don't show in the GUI
                                        //showError(response.responseJSON.message);
                                     });
}

/**
 * This function will load the Navigation Warning geometry onto the drawnItems
 * variable so that it is shown in the maps layers.
 *
 * @param {String}        xmlString       The Navigational Warning XML to be drawn on the map
 */
function loadNavWarnGeometry(headers, xmlString) {
    // Parse the Navigational Warning information
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "text/xml");
    const geometries = navwarnParser.parseWarnings(xmlDoc);

    // Get the signature information
    var signedBy = headers["signed-by"]? headers["signed-by"].replaceAll('\\c', '.') : 'none';
    var issuedBy = headers["issued-by"]? headers["issued-by"].replaceAll('\\c', '.') : 'none';

    for (const geometry of geometries) {
        if (geometry.type === 'point') {
            subscriptionMap.addLayer(this.createMarker(geometry.geometry, navWarnIcon, signedBy, issuedBy, xmlString));
        } else if (geometry.type === 'curve') {
            const line = L.polyline(geometry.geometry, lineStyle);
            navWarnAreas.push(line);
            subscriptionMap.addLayer(line);
            // Curve portrayal doesn't actually define a symbol, but let's use the surface symbol to have a click target.
            subscriptionMap.addLayer(this.createMarker(line.getCenter(), navWarnPointIcon, signedBy, issuedBy, xmlString));
        } else if (geometry.type === 'surface') {
            const polygon = L.polygon(geometry.geometry, surfaceStyle);
            navWarnAreas.push(polygon);
            subscriptionMap.addLayer(polygon);
            subscriptionMap.addLayer(this.createMarker(polygon.getCenter(), navWarnPointIcon, signedBy, issuedBy, xmlString));
        }
    }
}

//Creates a clickable marker (warning icon) and adds it to the map
function createMarker(center, icon, signedBy, issuedBy, xmlString, isWholeNAVAREA = false) {
    var lat = center.lat ? center.lat : center[0];
    var lng = center.lng ? center.lng : center[1];
    var positionKey = lat + '_' + lng;

    var newMarker = L.marker([lat, lng], { icon: icon });
    if (isWholeNAVAREA) {
        newMarker = new L.Marker(center, {
            icon: new L.DivIcon({
                className: 'my-div-icon',
                html: '<div style="text-align: center; margin: 0; padding: 0; background: transparent;">' +
                    '<img src="warning.png" style="width: 64px; height: 64px; margin: 0; padding: 0; border: none;"/>' +
                    '<div style="text-align: center; width: 100%; margin-top: 5px; padding: 0; border: none; background: transparent; line-height: 1.2; white-space: nowrap; font-weight: bold;">NAVAREA X</div>' +
                    '</div>',
                iconAnchor: [32, 32]
            }),
        });
    }

    if (!markersByPosition.has(positionKey)) {
        markersByPosition.set(positionKey, []);
    }

    markersByPosition.get(positionKey).push({ marker: newMarker, data: xmlString });

    newMarker.on('click', () => {
        var markers = markersByPosition.get(positionKey);

        // Update the signature information
        $('#signed-by').text(signedBy);
        $('#issued-by').text(issuedBy);

        if (markers.length === 1) {
            var dataObject = navwarnParser.parseDataToTable(markers[0].data)
            // And show the info
            showInfoTable([dataObject]);
        }
        else {
            // If there are multiple markers at this position, show a popup with a list (OptionPopup)
            var dataObjects = [];
            markers.forEach((marker, index) => {
                dataObjects.push(navwarnParser.parseDataToTable(marker.data));
            });
            // And show the info
            showInfoTable(dataObjects);
        }
    });

    return newMarker;
}

/**
 * This function will load the AtoN geometry onto the drawnItems variable
 * so that it is shown in the maps layers.
 *
 * @param {String}        type          The type of the AtoN objects to be drawn on the map
 * @param {Object}        aton          The AtoN objects to be drawn on the map
 */
function loadAtoNGeometry(headers, aton) {
    // Get the display type and name for the AtoN
    var type = headers["aton-type"];
    var displayName = aton.featureNames.find(f => f.displayName);
    var iconUrl = computeAtonIconUrl(type, aton);

    // Get the signature information
    var signedBy = headers["signed-by"]? headers["signed-by"].replaceAll('\\c', '.') : 'none';
    var issuedBy = headers["issued-by"]? headers["issued-by"].replaceAll('\\c', '.') : 'none';

    // If this is not a top-level object skip
    if(aton.parent) {
        return;
    }

    // Get an icon through Niord
    var atonIcon = L.icon({
        iconUrl: iconUrl,
        iconSize: [64, 64]
    });

    // Generate the map marker
    var atonMarker = L.marker([
                aton.geometries[0].pointProperty.point.pos.value[0],
                aton.geometries[0].pointProperty.point.pos.value[1]
            ], /*{icon: atonIcon}*/) // Disable the icons in the demo
            .addTo(subscriptionMap);

    // Show the AtoN information on click
    atonMarker.on('click', () => {
        // Update the signature information
        $('#signed-by').text(signedBy);
        $('#issued-by').text(issuedBy);

        // And show the AtoN information
        showInfoTable([atonParser.parseDataToTable(type, aton)]);
    });

    // And add the new marker in the satellite position markers
    atonMarkers.push(atonMarker);
}

/**
 * Clears all the AtoN markers from the GUI map.
 */
function clearMapMarkers() {
    // First check that we have a map
    if(subscriptionMap == undefined) {
        return;
    }

    // Clear the AtoN markers that have already been added
    if (atonMarkers != undefined) {
        for(var i=atonMarkers.length-1; i >= 0; i--) {
            subscriptionMap.removeLayer(atonMarkers[i]);
            atonMarkers.splice(i, 1);
        }
    };

    // Clear the Navigational Warning areas that have already been added
    if (navWarnAreas != undefined) {
        for(var i=navWarnAreas.length-1; i >= 0; i--) {
            subscriptionMap.removeLayer(navWarnAreas[i]);
            navWarnAreas.splice(i, 1);
        }
    };

    // Clear the Navigational Warning markers
    if(markersByPosition != undefined) {
        var keys = markersByPosition.keys();
        // For each key we can have multiple markers
        while(markersByPosition.size > 0) {
            var pos = keys.next().value;
            var markers = markersByPosition.get(pos);
            for(var i=markers.length-1; i >= 0; i--) {
                subscriptionMap.removeLayer(markers[i].marker);
            }
            markersByPosition.delete(pos)
        }
    }
}

/**
 * This function will generate a URL to display an icon for the AtoN in
 * question. This is achieved through some interaction with the DMA Niord
 * system that uses a JOSM module to achieve this.
 *
 * @param {Object}        aton          The AtoN object to generate the icon for
 */
function computeAtonIconUrl(type, aton) {
    if (!type) {
        return '/images/aton.png';
    }

    // Add the seamark type if that is available
    var url = this.addParam('', 'seamark:type', type);

    // Add the seamark sub-type (for each supported type) if that is available
    if(aton.categoryOfLandmark) {
        url = this.addParam(url, 'seamark:' + type + ':category', aton.categoryOfLandmark.toLowerCase());
    }
    else if(aton.categoryOfInstallationBuoy) {
        // Choose one of the two types
        if(aton.categoryOfInstallationBuoy == 'CATENARY_ANCHOR_LEG_MOORING_CALM') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'calm');
        } else if(aton.categoryOfInstallationBuoy == 'SINGLE_BUOY_MOORING_SBM_OR_SPM') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'sbm');
        }
    }
    else if(aton.categoryOfSpecialPurposeMark) {
        // Format the category
        var categoryOfSpecialPurposeMark = aton.categoryOfSpecialPurposeMark.replace('_MARK','').trim().toLowerCase();
        url = this.addParam(url, 'seamark:' + type + ':category', categoryOfSpecialPurposeMark);
    }
    else if(aton.categoryOfLateralMark) {
        // Choose one of the four types
        if(aton.categoryOfLateralMark == 'PORT_HAND_LATERAL_MARK') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'port');
        } else if(aton.categoryOfLateralMark == 'STARBOARD_HAND_LATERAL_MARK') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'starboard');
        } else if(aton.categoryOfLateralMark == 'PREFERRED_CHANNEL_TO_PORT_LATERAL_MARK') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'preferred_channel_port');
        } else if(aton.categoryOfLateralMark == 'PREFERRED_CHANNEL_TO_STARBOARD_LATERAL_MARK') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'preferred_channel_starboard');
        }
    }
    else if(aton.categoryOfCardinalMark) {
        // Format the category
        var categoryOfCardinalMark = aton.categoryOfCardinalMark.replace('_CARDINAL_MARK','').toLowerCase();
        url = this.addParam(url, 'seamark:' + type + ':category', categoryOfCardinalMark);
    }
    else if(aton.virtualAISAidToNavigationType) {
        if(aton.virtualAISAidToNavigationType == 'NEW_DANGER_MARKING') {
            url = this.addParam(url, 'seamark:' + type + ':category', 'wreck');
        } else {
            url = this.addParam(url, 'seamark:' + type + ':category', aton.virtualAISAidToNavigationType.replace('_CHANNEL_TO','').toLowerCase());
        }
    }

    // Add the shape seamark entry if that is available
    if(aton.shape) {
        url = this.addParam(url, 'seamark:' + type + ':shape', aton.shape.toLowerCase());
    } else if(aton.buoyShape) {
        // Choose one of the multiple types
        if(aton.buoyShape == 'CONICAL_NUN_OGIVAL') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'conical');
        } else if(aton.buoyShape == 'CAN_CYLINDRICAL') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'can');
        } else if(aton.buoyShape == 'SPHERICAL') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'spherical');
        } else if(aton.buoyShape == 'SUPER_BUOY') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'super-buoy');
        } else if(aton.buoyShape == 'PILLAR') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'pillar');
        } else if(aton.buoyShape == 'SPAR_SPINDLE') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'spar');
        } else if(aton.buoyShape == 'BARREL_TUN') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'barrel');
        } else if(aton.buoyShape == 'ICE_BUOY') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'ice-buoy');
        }
    } else if(aton.beaconShape) {
        // Choose one of the multiple types
        if(aton.beaconShape == 'STAKE_POLE_PERCH_POST') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'stake');
        } else if(aton.beaconShape == 'BEACON_TOWER') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'stake');
        } else if(aton.beaconShape == 'LATTICE_BEACON') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'lattice');
        } else if(aton.beaconShape == 'PILE_BEACON') {
            url = this.addParam(url, 'seamark:' + type + ':shape', 'pile');
        }
    }

    // Add the colour seamark entry if that is available
    if(aton.colours) {
        url = this.addParam(url, 'seamark:' + type + ':colour', aton.colours.join(';').toLowerCase());
    }

    // Add the colour-pattern seamark entry if that is available
    if(aton.colourPatterns) {
        // Format the colour patterns
          var colourPatterns = aton.colourPatterns
            .join(';')
            .replace('HORIZONTAL_STRIPES','horizontal')
            .replace('VERTICAL_STRIPES','vertical')
            .replace('DIAGONAL_STRIPES','diagonal')
            .replace('SQUARED','squared')
            .replace('STRIPES_DIRECTION_UNKNOWN','stripes')
            .replace('BORDER_STRIPE','border')
            .replace('SINGLE_COLOUR','single')
            .toLowerCase();
        url = this.addParam(url, 'seamark:' + type + ':colour_pattern', colourPatterns);
    }

    // And return the constructed URL
    return baseIconApiUrl + '/rest/aton-icon/overview?' + url;
}

/**
 * Adds a the given AtoN tag as a parameter if well-defined.
 *
 * @param url           The parameter URL
 * @param aton          The AtoN in question
 * @param k             The AtoN tag key
 */
function addParam(url, k, v) {
    if (url.length > 0) {
        url = url + '&';
    }
    url += encodeURIComponent(k) + '=' + encodeURIComponent(v);
    return url;
}
