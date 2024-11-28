/**
 * This class contains a number of helper functions to parse and process S-124 data.
 */
class NWParser {

    //Navarea geometry
    navareaX = [
        [-60.0000, 80.0000],
        [-30.0000, 80.0000],
        [-30.0000, 95.0000],
        [-12.0000, 95.0000],
        [-12.0000, 127.0000],
        [-10.0000, 127.0000],
        [-10.0000, 141.0000],
        [0.0000, 141.0000],
        [0.0000, 170.0000],
        [-29.0000, 170.0000],
        [-45.0000, 160.0000],
        [-60.0000, 160.0000],
        [-60.0000, 80.0000]
    ];

    static NS_S100 = 'http://www.iho.int/s100gml/5.0';
    static NS_S124 = 'http://www.iho.int/S124/1.0';
    static NS_GML = 'http://www.opengis.net/gml/3.2';

    /**
     * Returns the ID of a warning. This will be extracted from the NAVWARNPreamble.
     * Make sure that your S-124 generator assigns an id to this attribute.
     * @param {*} xmlData the gml
     * @returns the id
     */
    getId(xmlData) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlData, "text/xml");
        const elements = xmlDoc.getElementsByTagNameNS('*', "NAVWARNPreamble");

        if (elements.length > 0) {
            const attributes = elements[0].attributes;
            for (let i = 0; i < attributes.length; i++) {
                const attributeName = attributes[i].name;
                if (attributeName.endsWith(":id")) {
                    return attributes[i].value;
                }
            }
        }

        // Return null indicating the attribute was not found
        return null;
    }

    /**
     * TODO: This is a workaround for a faulty auto-generation of the S-124 dataset references model.
     * In this implementation, it is checked if the warning contains a specific text and a reference to the id of the cancelled warning.
     * @param {*} xmlData
     * @returns
     */
    getCancellation(xmlData) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlData, "text/xml");
        const elements = xmlDoc.getElementsByTagNameNS('*', "text"); //TODO: This is really just a fix for the missing references part in the data model..
        // Define a regular expression pattern to match the message
        const pattern = /^Cancellation of Warning with id ([a-fA-F0-9]+)\./;
        if (elements.length > 0) {
            const message = elements[0].textContent;
            const match = pattern.exec(message);

            if (match) {
                // The ID is in the first capturing group (index 1)
                const id = match[1];
                return id;
            }
        }
        return null;

        /*const elements = xmlDoc.getElementsByTagNameNS('*', "NAVWARNPreamble");

        if (elements.length > 0) {
          const attributes = elements[0].attributes;
          for (let i = 0; i < attributes.length; i++) {
            const attributeName = attributes[i].name;
            if (attributeName.endsWith(":id")) {
              return attributes[i].value;
            }
          }
        }*/
    }

    /**
     * Returns the text content of a S-124 warning
     * @param {*} xmlData the gml
     * @returns the text content of a S-124 dataset
     */
    getTextContent(xmlData) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlData, "text/xml");
        const elements = xmlDoc.getElementsByTagNameNS('*', "text");

        if (elements.length > 0) {
            const message = elements[0].textContent;
            // Split the message into lines using '\n' as the delimiter
            const lines = message.split('\n');

            // Remove the first three lines
            const formattedMessage = lines.slice(3, -2).join('\n');

            return formattedMessage;
        }
        return null;
    }


    /**
     * Extracts the most relevant information from a S-124 dataset and returns a table that can be displayed.
     * @param {*} xmlData the gml
     * @returns a table with the most relevant S-124 attributes
     */
    parseDataToTable(xmlData) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlData, "text/xml");

        const getProductData = (xmlDoc, tagName) => {
            const elements = xmlDoc.getElementsByTagNameNS('*', tagName);
            if (tagName == 'navwarnTypeGeneral' || tagName == 'navwarnTypeDetails')
                return elements.length > 0 ? (elements[0].getAttribute('code') != null ? elements[0].getAttribute('code') + ' - ' + elements[0].textContent : 'N/A') : 'N/A'
            return elements.length > 0 ? elements[0].textContent : 'N/A';
        };

        const tableData = {
            'id': 'NW-' + getProductData(xmlDoc, 'warningNumber'),
            'Data model': getProductData(xmlDoc, 'productIdentifier'),
            'Version': getProductData(xmlDoc, 'productEdition'),
            'S-100 profile': getProductData(xmlDoc, 'applicationProfile'),
            'Dataset title': getProductData(xmlDoc, 'datasetTitle'),
            'Area identifier': getProductData(xmlDoc, 'localityIdentifier'),
            'Responsible agency': getProductData(xmlDoc, 'agencyResponsibleForProduction'),
            'Warning series': getProductData(xmlDoc, 'nameOfSeries'),
            '# of warning': getProductData(xmlDoc, 'warningNumber'),
            'Warning type': getProductData(xmlDoc, 'warningType'),
            'Type of warning': getProductData(xmlDoc, 'navwarnTypeGeneral'),
            'Detailed Type': getProductData(xmlDoc, 'navwarnTypeDetails'),
            'Valid from': getProductData(xmlDoc, 'dateStart')?.trim(),
            'Valid to': getProductData(xmlDoc, 'dateEnd')?.trim(),
            'Language': getProductData(xmlDoc, 'language'),
            'Warning text': getProductData(xmlDoc, 'text'),
        };

        return tableData;
    }

    parseGeometries(element) {
        function chunk(array, chunkSize) {
            if (array.length % chunkSize !== 0) {
                throw new Error(`Unable to chunk. Array length ${array.length} not multiple of ${chunkSize}.`);
            }
            const chunks = [];
            for (let i = 0; i < array.length; i += chunkSize) {
                chunks.push([array[i], array[i+1]])
            }
            return chunks;
        }

        function postListToLatLng(posList) {
            const numbers = posList.split(/\s+/).map(number => parseFloat(number));
            return chunk(numbers, 2);
        }

        function posToLatLng(pos) {
            return postListToLatLng(pos)[0];
        }

        const result = [];

        const geometries = element.getElementsByTagNameNS(NWParser.NS_S124, "geometry");

        if (geometries.length === 0) {
            // If no geometry was provided, return default regional polygon.
            return {
                type: 'surface',
                geometry: this.navareaX,
            };
        }

        for (const geometry of geometries) {
            const prop = geometry.firstElementChild;
            const tag = prop.localName;
            if (tag === 'pointProperty') {
                const pos = geometry.getElementsByTagNameNS(NWParser.NS_GML, 'pos')[0];
                const coords = posToLatLng(pos.textContent);
                result.push({
                    type: 'point',
                    geometry: coords,
                });
            } else if (tag === 'curveProperty') {
                // TODO: Implement more complicated curves, and curves defined with individual pos elements.
                const posList = geometry.getElementsByTagNameNS(NWParser.NS_GML, 'posList')[0];
                if (!posList) {
                    throw new Error("Could not find posList in curveProperty.");
                }
                const coordsList = postListToLatLng(posList.textContent);
                result.push({
                    type: 'curve',
                    geometry: coordsList,
                });
            } else if (tag === 'surfaceProperty') {
                // TODO: Implement more complicated suraces, surfaces with holes and surfaces defined with pos elements.
                const posList = geometry.getElementsByTagNameNS(NWParser.NS_GML, 'posList')[0];
                if (!posList) {
                    throw new Error("Could not find posList in curveProperty.");
                }
                const coordsList = postListToLatLng(posList.textContent);
                result.push({
                    type: 'surface',
                    geometry: coordsList,
                });
            } else {
                throw new Error(`Could not parse geometry, unknown tag ${tag}`);
            }
        }

        return result;
    }

    parseWarnings(xmlDoc) {
        const result = [];
        const warnings = xmlDoc.getElementsByTagNameNS(NWParser.NS_S124, 'NAVWARNPart');
        for (const warning of warnings) {
            result.push(...this.parseGeometries(warning));
        }
        return result;
    }

    /**
     * Checks if a S-124 dataset contains a geometry
     * @param {*} xmlString the S-124 gml
     * @returns true if the gml dataset contains a geometry
     */
    hasGeometry(xmlString) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "text/xml");
        const posList = xmlDoc.getElementsByTagNameNS('*', 'posList'); //Polygon
        //Point
        if (posList.length != 0) {
            return true;

        } else {
            const posPoint = xmlDoc.getElementsByTagNameNS('*', 'pos'); //Point
            if (posPoint.length > 0) {
                return true;
            }

            return false;

        }
    }

    /**
     * Checks if a S-124 dataset is an in-force bulletin
     * @param {*} xmlString the S-124 gml
     * @returns true if the S-124 dataset is an in-force bulletin
     */
    isInForceBulletin(xmlString) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "text/xml");
        const elements = xmlDoc.getElementsByTagNameNS('*', 'navwarnTypeGeneral');
        if (elements.length > 0) {
            const category = elements[0].textContent;
            if (category == 'In-Force Bulletin')
                return true;
        }
        return false;
    }


    /**
     * Calculates the center of a polygon geometry
     * @param {*} polygon
     * @returns
     */
    calculatePolygonCenter(polygon) {
        if (!Array.isArray(polygon) || polygon.length < 3) {
            throw new Error("Invalid polygon data.");
        }

        let totalLat = 0;
        let totalLng = 0;

        for (let i = 0; i < polygon.length; i++) {
            const point = polygon[i];
            if (!Array.isArray(point) || point.length !== 2) {
                throw new Error("Invalid coordinate format.");
            }

            totalLat += point[0];
            totalLng += point[1];
        }

        const centerLat = totalLat / polygon.length;
        const centerLng = totalLng / polygon.length;

        return [centerLat, centerLng];
    }

    getCenter(xmlString) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "text/xml");
        const geometries = this.parseGeometries(xmlDoc);

        for (const geometry of geometries) {
            if (geometry.type === 'point') {
                return geometry.geometry;
            } else if (geometry.type === 'curve') {
                return L.latLngBounds(geometry.geometry).getCenter();
            } else if (geometry.type === 'surface') {
                return L.latLngBounds(geometry.geometry).getCenter();
            }
        }
    }
}