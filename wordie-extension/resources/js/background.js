function updateBadge(status) {
    if (status) {
        chrome.browserAction.setBadgeText({'text': 'on'});
        chrome.browserAction.setBadgeBackgroundColor({'color': '#00FF00'});
    } else {
        chrome.browserAction.setBadgeText({'text': 'off'});
        chrome.browserAction.setBadgeBackgroundColor({'color': '#FF0000'});
    }
};

chrome.storage.local.get('wordieEnabled', function(result) {
    var wordieEnabled = result.wordieEnabled ? result.wordieEnabled : false;
    updateBadge(wordieEnabled);
});

chrome.browserAction.onClicked.addListener(function() {

    chrome.storage.local.get('wordieEnabled', function(result) {
        var wordieEnabled = result.wordieEnabled ? result.wordieEnabled : false;
        chrome.storage.local.set({'wordieEnabled': !wordieEnabled}, function() {
            updateBadge(!wordieEnabled);
        });
        chrome.tabs.query({}, function(tabs) {
            for (var i=0; i < tabs.length; i++) {
                chrome.tabs.sendMessage(tabs[i].id, {'wordieEnabled': !wordieEnabled});
            };
        });
    });
    
});
