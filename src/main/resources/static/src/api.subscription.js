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
     * @param  {any} subscriptionRequest    The subscription request object
     * @param  {Function} callback          The callback to be used after the AJAX call
     * @param  {Function} errorCallback     The error callback to be used if the AJAX call fails
     */
    subscribe(url, subscriptionRequest, callback, errorCallback) {
        $.ajax({
            url: `${url}/v1/subscription`,
            type: 'POST',
            contentType: 'application/json',
            crossDomain: true,
            dataType: 'json',
            data: JSON.stringify(subscriptionRequest),
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Generate Subscription function.
     *
     * @param  {any} subscriptionRequest    The subscription request object
     * @param  {Function} callback          The callback to be used after the AJAX call
     * @param  {Function} errorCallback     The error callback to be used if the AJAX call fails
     */
    unsubscribe(callback, errorCallback) {
        $.ajax({
            url: `./api/subscription`,
            type: 'DELETE',
            contentType: 'application/json',
            crossDomain: true,
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }
}
/******************************************************************************/