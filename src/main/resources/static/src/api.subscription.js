/******************************************************************************
 *                               RAIM API CALLS                               *
 ******************************************************************************/
class SubscriptionApi {

    /**
     * The Raim API Class Constructor.
     */
    constructor() {

    }

    /**
     * API Generate Subscription function.
     *
     * @param  {string} url                     The SECOM service URL
     * @param  {any} subscriptionRequestObject  The subscription request object
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    subscribe(url, subscriptionRequestObject, callback, errorCallback) {
        $.ajax({
            url: `${url}/v1/subscription`,
            type: 'POST',
            contentType: 'application/json',
            crossDomain: true,
            data: JSON.stringify(subscriptionRequestObject),
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Remove Subscription function.
     *
     * @param  {string} url                     The SECOM service URL
     * @param  {any} removeSubscriptionObject   The remove subscription request object
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    unsubscribe(url, removeSubscriptionObject, callback, errorCallback) {
        $.ajax({
            url: `${url}/v1/subscription`,
            type: 'DELETE',
            contentType: 'application/json',
            crossDomain: true,
            data: JSON.stringify(removeSubscriptionObject),
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }
}
/******************************************************************************/