/**
 * This class contains a number of helper functions to parse and process S-125 data.
 */
class AtonParser {

    /**
     * Extracts the most relevant information from a S-125 dataset and returns
     * a table that can be displayed.
     *
     * @param   {String}     type The type of the S-125 AtoN
     * @param   {Object}     aton The JSON representation of an S-125 AtoN model
     * @returns a table with the most relevant S-125 attributes
     */
    parseDataToTable(type, aton) {
        const tableData = {
            'ID': aton.id,
            'ID Code': aton.idCode,
            'Name': toTitleCase(aton.featureNames[0].name),
            'Type': toTitleCase(type),
            'Information': toTitleCase(aton.informations[0].text),
            'Scale Minimum': aton.scaleMinimum,
            'Date Start': dateToSecomFormat(aton.dateStart),
            'Date End': dateToSecomFormat(aton.dateEnd),
            'Period Start':dateToSecomFormat(aton.periodStart),
            'Period End': dateToSecomFormat(aton.periodEnd),
            'Radar Conspicuous': toTitleCase(aton.radarConspicuous),
            'Status': toTitleCase(aton.statuses.join(", ")),
            'Marks Navigational System': aton.marksNavigationalSystemOf,
            'Seasonal Actions Required': toTitleCase(aton.seasonalActionRequireds.join(", "))
        };

        if(aton.buoyShape) {
            tableData['Buoy Shape']=toTitleCase(aton.buoyShape);
        }
        if(aton.colours) {
            tableData['Colour']=toTitleCase(aton.colours.join(", "));
        }
        if(aton.colourPatterns) {
            tableData['Colour Patterns']=toTitleCase(aton.colourPatterns.join(", "));
        }
        if(aton.categoryOfLateralMark) {
            tableData['Category of Lateral Mark']=toTitleCase(aton.categoryOfLateralMark);
        }
        if(aton.categoryOfCardinalMark) {
            tableData['Category of Cardinal Mark']=toTitleCase(aton.categoryOfCardinalMark);
        }

        return tableData;
    }
}
