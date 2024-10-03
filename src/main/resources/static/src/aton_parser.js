/**
 * This class contains a number of helper functions to parse and process S-125 data.
 */
class AtonParser {

    /**
     * Extracts the most relevant information from a S-125 dataset and returns
     * a table that can be displayed.

     * @param {Object} aton The JSON representation of an S-125 AtoN model
     * @returns a table with the most relevant S-125 attributes
     */
    parseDataToTable(aton) {
        const tableData = {
            'ID': aton.id,
            'ID Code': aton.idCode,
            'Name': aton.featureNames[0].name,
            'Information': aton.informations[0].text,
            'Scale Minimum': aton.scaleMinimum,
            'Date Start': dateToSecomFormat(aton.dateStart),
            'Date End': dateToSecomFormat(aton.dateEnd),
            'Period Start': dateToSecomFormat(aton.periodStart),
            'Period End': dateToSecomFormat(aton.periodEnd),
            'Radar Conspicuous': aton.radarConspicuous,
            'Status': aton.statuses.join(", "),
            'Marks Navigational System': aton.marksNavigationalSystemOf,
            'Seasonal Actions Required': aton.seasonalActionRequireds.join(", ")
        };

        if(aton.buoyShape) {
            tableData['Buoy Shape']=aton.buoyShape;
        }
        if(aton.colours) {
            tableData['Colour']=aton.colours;
        }
        if(aton.colourPatterns) {
            tableData['Colour Patterns']=aton.colourPatterns;
        }
        if(aton.categoryOfLateralMark) {
            tableData['Category of Lateral Mark']=aton.categoryOfLateralMark;
        }
        if(aton.categoryOfCardinalMark) {
            tableData['Category of Cardinal Mark']=aton.categoryOfCardinalMark;
        }

        return tableData;
    }

}