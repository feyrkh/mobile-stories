function renderCurrentCovenStory() {
    $.get("currentStory", function (data) {
        renderStory($("#mainContent"), data);
    });
}

function renderStory($el, data) {
    $el.html(data);
    $el.find(".disabled input").prop("disabled", true);
    $el.find(".disabled select").prop("disabled", true);
}

function performAction(actionId) {
    var $el = $("#mainContent");
    $.post("action/" + actionId, function (data) {
        renderStory($el, data);
    })
        .fail(function () {
            $el.html("Sorry, please refresh the page.")
        })
        .always(function () {
        });
}

function travelTo(categoryId) {
    var $el = $("#mainContent");
    $.post("action/travel/" + categoryId, function (data) {
        renderStory($el, data);
    })
        .fail(function () {
            $el.html("Sorry, please refresh the page.")
        })
        .always(function () {
        });
}

function chooseActionOption(actionId, optionIndex) {
    var $el = $("#mainContent");
    $.post({
        url: "choice/" + actionId,
        dataType: "html",
        data: JSON.stringify(getOptionChoiceData(actionId, optionIndex)),
        contentType: "application/json"
    }).done(
        function (data) {
            $el.html(data);
        })
        .fail(function (data) {
            $el.html("Sorry an error has occurred, please refresh the page.<br>" + JSON.stringify(data))
        })
        .always(function () {
        });
}

function getOptionChoiceData(actionId, optionIndex) {
    var $optionFields = $("#storyOptionContainer" + optionIndex + " .field");
    var formValues = {};

    $optionFields.each(function (idx, optionField) {
        var $optionField = $(optionField);
        formValues[optionField.name] = $optionField.val();
    });

    var data = {
        "actionId": actionId,
        "choiceId": optionIndex,
        "formValues": formValues
    };
    return data;
}