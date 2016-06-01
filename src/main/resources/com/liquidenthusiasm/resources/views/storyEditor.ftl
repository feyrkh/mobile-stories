<#-- @ftlvariable name="varRepo" type="java.lang.String" -->
<#-- @ftlvariable name="actionRepo" type="java.lang.String" -->
<#-- @ftlvariable name="functionRepo" type="java.lang.String" -->
<#-- @ftlvariable name="nextStoryId" type="java.lang.Long" -->
<#-- @ftlvariable name="actionCategorySelectOptions" type="java.lang.String" -->

<html>
<head>

    <script src="assets/jquery-2.2.3.min.js"></script>
    <script>
        var varRepo = ${varRepo};
        var storyRepo = ${actionRepo};
        var functionRepo = ${functionRepo};
        var nextStoryId = ${nextStoryId};
        var stateToggles = null;
        var optionToggles = null;
        var storyTemplate = '{' +
                '"actionCategory": "Ledgers",' +
                '"actionId":${nextStoryId},' +
                '"actionName": "NewStory",' +
                '"enableTriggers": [{"conditions":["ci_treasury>5"]}],' +
                '"viewTriggers": [{"conditions":["ci_treasury>0"]}],' +
                '"states":[' +
                '{"id":0,' +
                '"canCancel":true,' +
                '"heading":"Starting state",' +
                '"text": "A story has begun!",' +
                '"options":[{' +
                '  "heading": "Default_Option",' +
                '  "timeCost": 0,' +
                '  "text": "",' +
                '  "transitions": ["INVALID_STATE"]' +
                '}]' +  // end of options arr
                '}]' + // end of states arr
                '}';
        console.log(storyTemplate);
        var currentStory = JSON.parse(storyTemplate);

        var stateSelect = '';


        function updateStoryJson() {
            $('#storyJson').val(JSON.stringify(currentStory, null, 2));
        }

        storyTemplate = JSON.stringify(currentStory, null, 2);
        var varFilter = '';

        function listVars() {
            $el = $('#varColumn');
            $ul = $('<ul></ul>');
            $el.empty();
            $el.append('<b>Variables</b>');
            var $varFilter = $('<input type="text" />');
            $varFilter.val(varFilter);
            $varFilter.change(function () {
                varFilter = $varFilter.val();
                listVars();
            });
            $el.append($varFilter);
            $el.append('<br>')
            $el.append($ul);
            $.each(varRepo, function (idx, variable) {
                if (varFilter && variable.name.indexOf(varFilter) < 0) return;
                var html = '<li><img width="16" height="16" src="' + variable.image + '"/> ' + variable.name;
                if (variable.label != variable.name) {
                    html += ': ' + variable.label;
                }
                html += '</li>';
                var $newEl = $(html);
                $newEl.prop('title', variable.desc);
                $ul.append($newEl);
            });
        }

        function listFunctions() {
            $el = $('#fnColumn');
            $ul = $('<ul></ul>');
            $el.empty();
            $el.append('<b>Functions</b><br>');
            $el.append($ul);
            $.each(functionRepo, function (fnName, args) {
                var html = ['<li>', fnName];
                if (args.requiredInputs && args.requiredInputs.length > 0) {
                    html.push('<ul>');
                    $.each(args.requiredInputs, function (idx, requiredInput) {
                        html.push('<li>&rarr;&lt;');
                        html.push(requiredInput);
                        html.push('&gt;</li>');
                    });
                    html.push('</ul>');
                }
                if (args.optionalInputs && args.optionalInputs.length > 0) {
                    html.push('<ul>');
                    $.each(args.optionalInputs, function (idx, optionalInput) {
                        html.push('<li>&rarr;[');
                        html.push(optionalInput);
                        html.push(']</li>');
                    });
                    html.push('</ul>');
                }
                if (args.outputs && args.outputs.length > 0) {
                    html.push('<ul>');
                    $.each(args.outputs, function (idx, output) {
                        html.push('<li>&larr; ');
                        html.push(output);
                        html.push('</li>');
                    });
                    html.push('</ul>');
                }
                html.push('</li>');
                $ul.append(html.join(''));
            });
        }

        function listStories() {
            var $el = $("#storySelect");
            $el.empty();
            $el.append("<option value='-1'>New Story</option>")
            $.each(storyRepo, function (idx, story) {
                $el.append("<option value='" + idx + "'>" + story.filename + ": " + story.actionName + "</option>")
            });
        }

        function changeStory() {
            var storyIdx = $('#storySelect').val();
            if (storyIdx < 0) {
                $('#storyJson').val(storyTemplate);
            } else {
                var selectedStory = storyRepo[storyIdx];
                $('#storyJson').val(JSON.stringify(selectedStory, null, 2));
            }
            stateToggles = null;
            optionToggles = null;
            updateStoryRender();
        }

        var renderActionId = function ($newStory, actionId) {
            $newStory.append("<div class='actionId'>actionId: " + actionId + "</div>")
        };

        var renderActionCategory = function ($newStory, actionCategory) {
            var $wrap = $("<div class='actionCategory'>actionCategory: </div>");
            var html = "<select class='actionCategory' id='actionCategory'>";
            html += "${actionCategorySelectOptions}";
            html += "</select>";
            var $newSelect = $(html);
            $wrap.append($newSelect);
            $newSelect.find('option[value="' + currentStory.actionCategory + '"]').prop('selected', true);
            $newSelect.change(function () {
                currentStory.actionCategory = $newSelect.val();
                updateStoryJson();
            });
            $newStory.append($wrap);
        };

        var renderCanCancel = function ($newStory, canCancel, state) {
            canCancel = !!canCancel;
            var $wrap = $("<span class='canCancel'> &nbsp; (cancelable?) </span>");
            var $new = $("<input type='checkbox' id='canCancel' />");
            $wrap.append($new);
            $new.prop('checked', canCancel);
            $new.change(function () {
                state.canCancel = $new.prop('checked');
                ;
                updateStoryJson();
            });
            $newStory.append($wrap);
        };


        var renderActionName = function ($newStory, actionName) {
            var $wrap = $("<div class='actionName'>actionName: </div>");
            var $new = $("<input type='text' id='actionName'/>");
            $wrap.append($new);
            $new.val(actionName);
            $new.change(function () {
                currentStory.actionName = $new.val();
                updateStoryJson();
            });
            $newStory.append($wrap);
        };

        var renderEnableTriggers = function ($newStory, enableTriggers, currentEntity) {
            var $wrap = $("<div class='enableTriggers'>enableTriggers: </div>");
            var $addTrigger = $("<span class='addEnableTrigger link'>(add enableTrigger)</span>");
            $wrap.append($addTrigger);
            $addTrigger.click(function (e) {
                e.preventDefault();
                if (!currentEntity.enableTriggers) currentEntity.enableTriggers = [];
                currentEntity.enableTriggers.push({"conditions": []});
                updateStoryJsonAndRender()
            });
            $.each(currentEntity.enableTriggers, function (triggerIdx, trigger) {

                function updateTrigger(triggerIdx, conditionIdx, newVal) {
                    currentEntity.enableTriggers[triggerIdx].conditions[conditionIdx] = newVal;
                    if (newVal == '') {
                        currentEntity.enableTriggers[triggerIdx].conditions.splice(conditionIdx, 1);
                    }
                    if (!currentEntity.enableTriggers[triggerIdx].conditions || currentEntity.enableTriggers[triggerIdx].conditions.length == 0) {
                        currentEntity.enableTriggers.splice(triggerIdx, 1);
                    }
                    if (!currentEntity.enableTriggers || currentEntity.enableTriggers.length == 0) {
                        delete currentEntity['enableTriggers'];
                    }
                    updateStoryJsonAndRender();
                }

                var $newTriggerWrap = $("<div class='enableTrigger'/>");
                $wrap.append($newTriggerWrap);
                $.each(trigger.conditions, function (conditionIdx, condition) {
                    var $newTrigger = $("<input type='text'/>");
                    $newTriggerWrap.append($newTrigger);
                    $newTriggerWrap.append("<span> and </span>");
                    $newTrigger.val(condition);
                    $newTrigger.change(function () {
                        updateTrigger(triggerIdx, conditionIdx, $newTrigger.val())
                    });
                });
                var $newTrigger = $("<input type='text'/>");
                $newTriggerWrap.append($newTrigger);
                $newTrigger.change(function () {
                    trigger.conditions.push($newTrigger.val());
                    updateStoryJsonAndRender();
                });
            });
            $newStory.append($wrap);
        };

        var renderViewTriggers = function ($newStory, viewTriggers, currentEntity) {
            var $wrap = $("<div class='viewTriggers'>viewTriggers: </div>");
            var $addTrigger = $("<span class='addViewTrigger link'>(add viewTrigger)</span>");
            $wrap.append($addTrigger);
            $addTrigger.click(function (e) {
                e.preventDefault();
                if (!currentEntity.viewTriggers) currentEntity.viewTriggers = [];
                currentEntity.viewTriggers.push({"conditions": []});
                updateStoryJsonAndRender()
            });
            $.each(currentEntity.viewTriggers, function (triggerIdx, trigger) {

                function updateTrigger(triggerIdx, conditionIdx, newVal) {
                    currentEntity.viewTriggers[triggerIdx].conditions[conditionIdx] = newVal;
                    if (newVal == '') {
                        currentEntity.viewTriggers[triggerIdx].conditions.splice(conditionIdx, 1);
                    }
                    if (!currentEntity.viewTriggers[triggerIdx].conditions || currentEntity.viewTriggers[triggerIdx].conditions.length == 0) {
                        currentEntity.viewTriggers.splice(triggerIdx, 1);
                    }
                    if (!currentEntity.viewTriggers || currentEntity.viewTriggers.length == 0) {
                        delete currentEntity['viewTriggers'];
                    }
                    updateStoryJsonAndRender();
                }

                var $newTriggerWrap = $("<div class='viewTrigger'/>");
                $wrap.append($newTriggerWrap);
                $.each(trigger.conditions, function (conditionIdx, condition) {
                    var $newTrigger = $("<input type='text'/>");
                    $newTriggerWrap.append($newTrigger);
                    $newTriggerWrap.append("<span> and </span>");
                    $newTrigger.val(condition);
                    $newTrigger.change(function () {
                        updateTrigger(triggerIdx, conditionIdx, $newTrigger.val())
                    });
                });
                var $newTrigger = $("<input type='text'/>");
                $newTriggerWrap.append($newTrigger);
                $newTrigger.change(function () {
                    trigger.conditions.push($newTrigger.val());
                    updateStoryJsonAndRender();
                });
            });
            $newStory.append($wrap);
        };


        var renderStoryStates = function ($newStory, states) {
            if (!stateToggles) {
                stateToggles = [];
                $.each(states, function () {
                    stateToggles.push(false);
                });
            }
            $newStory.append("<span><b>States <b></span>");
            var $createNewState = $('<a href="#">(add state)</a>');
            $newStory.append($createNewState);
            $newStory.append('<br>');
            $createNewState.click(function (e) {
                e.preventDefault();
                states.push({id: states.length, text: "STORY_GOES_HERE"});
                updateStoryJsonAndRender()
            });
            $.each(states, function (idx, state) {
                state.id = idx;
                var $stateWrapper = $('<div class="state"/>');
                var $stateHeader = $('<div class="stateHeader"/>');
                $stateHeader.append("<span>State #" + idx + ": " + state.heading + "</span>");
                renderCanCancel($stateHeader, state.canCancel, state);
                var $stateToggle = $("<a href='#'>(toggle state)</a>");
                $stateHeader.append($stateToggle);
                $stateToggle.click(function (e) {
                    e.preventDefault();
                    stateToggles[idx] = !stateToggles[idx];
                    if (stateToggles[idx]) {
                        $stateWrapper.show();
                    } else {
                        $stateWrapper.hide();
                    }
                });
                $newStory.append($stateHeader);
                $newStory.append($stateWrapper);
                renderStoryState($stateWrapper, state);

                if (stateToggles[idx]) {
                    $stateWrapper.show();
                } else {
                    $stateWrapper.hide();
                }
            })
        };

        var renderStoryState = function ($stateWrapper, state) {
            var $heading = $("<input type='text' class='stateHeading'/>");
            $stateWrapper.append($heading);
            var $delete = $('<a href="#">(delete state)</a>');
            $stateWrapper.append("<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            $stateWrapper.append($delete);
            $delete.click(function (e) {
                e.preventDefault();
                if (confirm("Are you sure you want to delete this entire story state?")) {
                    currentStory.states.splice(state.id, 1);
                    stateToggles.splice(state.id, 1);
                    var deletingId = state.id;
                    $.each(currentStory.states, function (stateIdx, modifyState) {
                        if (!modifyState.options) return;
                        $.each(modifyState.options, function (optionIdx, modifyOption) {
                            if (!modifyOption.transitions) return;
                            $.each(modifyOption.transitions, function (transitionIdx, modifyTransition) {
                                var toState = getTransitionState(modifyTransition);
                                var condition = getTransitionIf(modifyTransition);
                                if (toState == deletingId) {
                                    toState = 'INVALID_STATE';
                                } else if (parseInt(toState) > parseInt(deletingId)) {
                                    toState = parseInt(toState) - 1;
                                }
                                modifyOption.transitions[transitionIdx] = toState + condition;
                            });
                        });
                    });
                    updateStoryJsonAndRender()
                }
            });
            $stateWrapper.append("<br>");
            $heading.val(state.heading);
            $heading.change(function () {
                state.heading = $heading.val();
                updateStoryJsonAndRender();
            });
            var $text = $("<textarea class='stateText'></textarea>");
            $stateWrapper.append($text);
            $text.val(state.text);
            $text.change(function () {
                state.text = $text.val();
                updateStoryJson();
            });

            var $addWrapper = $('<div/>');
            $stateWrapper.append($addWrapper);
            var $statChanges = $('<textarea cols="120"></textarea>');
            $addWrapper.append("<span>Stat changes:</span>");
            $addWrapper.append($statChanges);
            var statChangeDesc = [];
            if (state.statAdds) {
                $.each(state.statAdds, function (idx, statAdd) {
                    statChangeDesc.push(statAdd);
                });
            }

            $statChanges.change(function (e) {
                var lines = $statChanges.val().split("\n");
                state.statAdds = lines;
                updateStoryJson();
            });

            $statChanges.val(statChangeDesc.join("\n"));

            var $optionWrapper = $('<div class="options"/>');
            $stateWrapper.append($optionWrapper);
            if (state.options == null) {
                state.options = [];
            }

            var $preSubmitWrapper = $('<div/>');
            $optionWrapper.append($preSubmitWrapper);
            $preSubmitWrapper.append('<i>Pre-submit function calls: </i>');
            var $addPreSubmitCall = $('<a href="#">(add preSubmitCall)</a>');
            $preSubmitWrapper.append($addPreSubmitCall);
            $preSubmitWrapper.append("<br/>");
            $addPreSubmitCall.click(function (e) {
                e.preventDefault();
                if (!state.preSubmitCalls) state.preSubmitCalls = [];
                state.preSubmitCalls.push({});
                updateStoryJsonAndRender()

            });
            $.each(state.preSubmitCalls, function (idx, functionCall) {
                renderFunctionCall($preSubmitWrapper, functionCall, idx, state.preSubmitCalls);
            });

            renderStateOptions($optionWrapper, state.options);

        };

        var renderStateOptions = function ($container, options) {
            $container.append("<span><b>Options <b></span>");
            var $createNewOption = $('<a href="#">(add option)</a>');
            $container.append($createNewOption);
            $container.append('<br>');
            $createNewOption.click(function (e) {
                e.preventDefault();
                options.push({"heading": "New option", "text": "", "fields": [], "postSubmitCalls": [], "transitions": ["0"]});
                updateStoryJsonAndRender()
            });
            $.each(options, function (idx, option) {
                var $optionWrapper = $('<div class="option"/>');
                $container.append($optionWrapper);
                $container.append('<hr/>')
                renderStoryOption($optionWrapper, option, idx, options);
            })
        };

        var renderTransitions = function ($transitionsWrapper, option) {
            var $addTransition = $('<a href="#">(add transition)</a>');
            $transitionsWrapper.append($addTransition);
            $transitionsWrapper.append('<br>');
            $addTransition.click(function (e) {
                e.preventDefault();
                if (!option.transitions) option.transitions = [];
                option.transitions.push("-1");
                updateStoryJsonAndRender();
            })


            $.each(option.transitions, function (idx, transition) {
                var $stateSelect = $(stateSelect);
                var curTransitionState = getTransitionState(transition);
                var $transWrap = $('<div/>');
                $transitionsWrapper.append($transWrap);
                $stateSelect.find('option[value="' + curTransitionState + '"]').prop('selected', true);
                $stateSelect.change(function () {
                    var transitionState = $stateSelect.val();
                    var transitionIf = getTransitionIf(transition);
                    option.transitions[idx] = transitionState + (transitionIf ? ' ' + transitionIf : '');
                    updateStoryJson();
                });
                $transWrap.append($stateSelect);
                var $ifStatement = $('<input type="text"/>');
                $transWrap.append($ifStatement);
                $ifStatement.val(getTransitionIf(transition));
                $ifStatement.change(function () {
                    var transitionState = $stateSelect.val();
                    var transitionIf = $ifStatement.val();
                    option.transitions[idx] = transitionState + (transitionIf ? ' ' + transitionIf : '');
                    updateStoryJson();
                });
            });
        };

        function getTransitionState(transition) {
            if (transition == null || transition.length == 0) {
                return -1;
            }
            return transition.split(' ', 2)[0];
        }

        function getTransitionIf(transition) {
            if (transition == null || transition.length == 0) {
                return -1;
            }
            var split = transition.split(' ');
            split.shift();
            if (split.length >= 1)
                return ' ' + split.join(' ');
            return '';
        }

        var renderRandomize = function ($randomizeWrapper, randomAdjustment, option) {
            if (randomAdjustment == null) {
                var $addRandomize = $('<a href="#">(add randomizer)</a>');
                $randomizeWrapper.append($addRandomize);
                $addRandomize.click(function (e) {
                    e.preventDefault();
                    option.randomAdjustment = {
                        "outputVar": "si_random",
                        "baseChance": 500,
                        "stat": "pi_luck",
                        "baseStat": 0,
                        "statAdjustment": 100
                    };
                    updateStoryJsonAndRender();
                });
            } else {
                var $outputVar = $('<input type="text"/>');
                var $baseChance = $('<input type="text"/>');
                var $stat = $('<input type="text"/>');
                var $baseStat = $('<input type="text"/>');
                var $statAdjustment = $('<input type="text"/>');
                var $delete = $('<a href="#">(delete)</a>');

                var update = function (e) {
                    e.preventDefault();
                    option.randomAdjustment = {
                        "outputVar": $outputVar.val(),
                        "baseChance": Math.round(parseFloat($baseChance.val())),
                        "stat": $stat.val(),
                        "baseStat": Math.round(parseFloat($baseStat.val())),
                        "statAdjustment": Math.round(parseFloat($statAdjustment.val()))
                    };
                    updateStoryJson();
                };

                $outputVar.val(randomAdjustment.outputVar);
                $baseChance.val(randomAdjustment.baseChance);
                $stat.val(randomAdjustment.stat);
                $baseStat.val(randomAdjustment.baseStat);
                $statAdjustment.val(randomAdjustment.statAdjustment);

                $outputVar.change(update);
                $baseChance.change(update);
                $stat.change(update);
                $baseStat.change(update);
                $statAdjustment.change(update);

                $delete.click(function (e) {
                    e.preventDefault();
                    delete option['randomAdjustment'];
                    updateStoryJsonAndRender();
                });

                $randomizeWrapper
                        .append("<span>Randomizer: </span>")
                        .append($outputVar)
                        .append("<span> = 1000 - (</span>")
                        .append($baseChance)
                        .append("<span> + (</span>")
                        .append($statAdjustment)
                        .append("<span> per point by which </span>")
                        .append($stat)
                        .append("<span> is greater than </span>")
                        .append($baseStat)
                        .append('<span>) &nbsp; </span>')
                        .append($delete);
            }
        };

        var renderStoryOption = function ($optionWrapper, option, idx, options) {
            var $deleteOption = $("<a href='#'>(delete option)</a>");
            $deleteOption.click(function (e) {
                e.preventDefault();
                if (confirm('Sure you want to delete this option?')) {
                    options.splice(idx, 1);
                    updateStoryJsonAndRender();
                }
            });
            $optionWrapper.append($deleteOption);
            $optionWrapper.append('<br>');

            $optionWrapper.append('<span>Heading: </span>');
            var $heading = $('<input type="text" size="50"/>');
            $heading.val(option.heading);
            $optionWrapper.append($heading);
            $heading.change(function () {
                option.heading = $heading.val();
                updateStoryJsonAndRender()
            });
            $optionWrapper.append("<span> Time cost: </span>");
            var $actionCost = $('<select><option value="0">0</option><option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="12">12</option><option value="16">16</option><option value="24">24</option></select>');
            $optionWrapper.append($actionCost);
            $optionWrapper.append('<br>');
            $actionCost.val(option.timeCost);
            $actionCost.find('option[value="' + option.timeCost + '"]').prop('selected', true);

            $actionCost.change(function () {
                option.timeCost = parseInt($actionCost.val());
                updateStoryJson();
            });

            $optionWrapper.append("<br>");
            $optionWrapper.append('<span>Text: </span>');
            var $text = $('<textarea class="optionText"></textarea>');
            $text.val(option.text);
            $optionWrapper.append($text);
            $text.change(function () {
                option.text = $text.val();
                updateStoryJson();
            });

            var $randomizeWrapper = $("<div class='randomize'/>");
            $optionWrapper.append($randomizeWrapper);
            renderRandomize($randomizeWrapper, option.randomAdjustment, option);

            var $addWrapper = $('<div/>');
            $optionWrapper.append($addWrapper);
            var $statChanges = $('<textarea cols="120"></textarea>');
            $addWrapper.append("<span>Post-submit stat changes:</span>");
            $addWrapper.append($statChanges);
            var statChangeDesc = [];
            if (option.statAdds) {
                $.each(option.statAdds, function (idx, statAdd) {
                    statChangeDesc.push(statAdd);
                });
            }

            $statChanges.change(function (e) {
                var lines = $statChanges.val().split("\n");
                option.statAdds = lines;
                updateStoryJson();
            });

            $statChanges.val(statChangeDesc.join("\n"));
            var $transitionsWrapper = $("<div class='transitions'/>");
            $optionWrapper.append($transitionsWrapper);
            renderTransitions($transitionsWrapper, option);

            var $fieldWrapper = $('<div class="fields"/>');
            $optionWrapper.append($fieldWrapper);
            $fieldWrapper.append("<i>Fields </i>");
            var $addField = $('<a href="#">(add field)</a>');
            $fieldWrapper.append($addField);
            $fieldWrapper.append('<br>');
            $addField.click(function (e) {
                e.preventDefault();
                if (!option.fields) option.fields = [];
                option.fields.push({
                    label: "New Field",
                    name: 'ss_new_field',
                    type: 'text',
                    'defaultValue': '{{ci_treasury}}',
                    validation: {},
                    width: 30
                });
                updateStoryJsonAndRender()
            });

            $.each(option.fields, function (idx, field) {
                renderOptionFields($fieldWrapper, field, idx, option.fields);
            })

            var $postSubmitWrapper = $('<div/>');
            $optionWrapper.append($postSubmitWrapper);
            $postSubmitWrapper.append('<i>Post-submit function calls: </i>');
            var $addPostSubmitCall = $('<a href="#">(add postSubmitCall)</a>');
            $postSubmitWrapper.append($addPostSubmitCall);
            $postSubmitWrapper.append("<br/>");
            $addPostSubmitCall.click(function (e) {
                e.preventDefault();
                if (!option.postSubmitCalls) option.postSubmitCalls = [];
                option.postSubmitCalls.push({});
                updateStoryJsonAndRender()

            });
            $.each(option.postSubmitCalls, function (idx, postSubmitCall) {
                renderFunctionCall($postSubmitWrapper, postSubmitCall, idx, option.postSubmitCalls);
            });

            var $enableTriggers = $('<div/>');
            renderEnableTriggers($optionWrapper, option.enableTriggers, option);
            renderViewTriggers($optionWrapper, option.viewTriggers, option);
        };

        var renderOptionFields = function ($fieldWrapper, field, fieldIdx, fields) {
            var $field = $('<textarea class="field"></textarea>');
            $field.val(JSON.stringify(field, null, 2));
            $field.change(function () {
                if ($field.val() == '') {
                    fields.splice(fieldIdx, 1);
                } else {
                    fields[fieldIdx] = JSON.parse($field.val());
                }
                updateStoryJsonAndRender()
            });
            $fieldWrapper.append($field);
            $fieldWrapper.append("<br/>");
        };

        function updateStoryJsonAndRender() {
            updateStoryJson();
            updateStoryRender();
        }

        var renderFunctionCall = function ($functionWrapper, functionCall, idx, functionCalls) {
            var $callWrapper = $('<div class="functionCall"/>');
            var $functionName = $(functionNameSelectHtml);
            $functionName.change(function () {
                if ($functionName.val() == 'DELETE' && confirm('Sure you want to delete this function call?')) {
                    functionCalls.splice(idx, 1);
                } else {
                    functionCall.function = $functionName.val();
                }
                updateStoryJsonAndRender()
            });

            $functionName.find('option[value="' + functionCall.function + '"]').prop('selected', true);
            $functionWrapper.append($callWrapper);
            $callWrapper.append($functionName);

            $.each(functionCall.inputs, function (idx, inputStr) {
                var $val = $('<input class="functionCallArg" type="text" />');
                $callWrapper.append($val);
//                $callWrapper.append("<br>");
                $val.val(inputStr);
                $val.change(function () {
                    if (!$val.val()) {
                        functionCall.inputs.splice(idx, 1);
                        if (functionCall.inputs.length == 0) {
                            delete functionCall['inputs'];
                        }
                        updateStoryJsonAndRender();
                    } else {
                        functionCall.inputs[idx] = $val.val();
                    }
                });
            });

            var $newCallInput = $('<input class="functionCallArg" type="text" />');
            $callWrapper.append($newCallInput);
            $newCallInput.change(function () {
                if (!functionCall.inputs) functionCall.inputs = [];
                functionCall.inputs.push($newCallInput.val());
                updateStoryJsonAndRender();
            });
        };

        var functionNameSelectHtml;

        function updateStoryRender() {
            var $el = $('#storyRender');
            $el.css('background-color', 'pink');
            currentStory = JSON.parse($('#storyJson').val());
            var $newStory = $('<div/>');

            stateSelect = '<select><option value="-1">--End story--</option><option value="INVALID_STATE">--INVALID_STATE--</option>';
            $.each(currentStory.states, function (idx, curVal) {
                stateSelect += '<option value="' + idx + '">' + curVal.heading + " (" + idx + ")</option>";
            });
            stateSelect += "</select>";

            functionNameSelectHtml = '<select class="functionName"><option value="DELETE">-- delete --</option>';
            $.each(functionRepo, function (fnName, args) {
                functionNameSelectHtml += '<option value="' + fnName + '">' + fnName + '</option>';
            })
            functionNameSelectHtml += "</select>";

            renderActionId($newStory, currentStory.actionId);
            renderActionCategory($newStory, currentStory.actionCategory);
            renderActionName($newStory, currentStory.actionName);
            renderEnableTriggers($newStory, currentStory.enableTriggers, currentStory);
            renderViewTriggers($newStory, currentStory.viewTriggers, currentStory);
            renderStoryStates($newStory, currentStory.states);

            $el.empty();
            $el.append($newStory);
            $el.css('background-color', 'white');
        }

        $(listVars);
        $(listFunctions);
        $(listStories);
        $(function () {
            $('#storySelect').change(changeStory);
        });
        $(function () {
            $('#storyJson').change(updateStoryRender);
            $('#storyJson').val(storyTemplate);
            updateStoryRender();
        })
    </script>
    <style>
        #varColumn {
            width: 300px;
            height: 400px;
            overflow-y: scroll;
            overflow-x: auto;
        }

        #fnColumn {
            width: 300px;
            height: 400px;
            overflow-y: scroll;
            overflow-x: auto;
        }

        #storyJson {
            width: 400px;
            height: 800px;
            overflow-y: scroll;
            float: left;
        }

        #storyRender {
            width: 600px;
            height: 800px;
            overflow-y: scroll;
        }

        #storyRenderContainer {
            float: left;
        }

        .link {
            text-decoration: underline;
            color: blue;
            cursor: pointer;
        }

        .enableTrigger {
            border: 1px solid lightgray;
        }

        .viewTrigger {
            border: 1px solid lightgray;
        }

        .enableTrigger input {
            width: 160px;
        }

        .viewTrigger input {
            width: 160px;
        }

        .state {
            border: 1px solid darkgray;
            margin-left: 20px;
            margin-bottom: 20px;
        }

        .option {
            border: 1px solid lightgray;
            margin-left: 40px;
            margin-bottom: 20px;
        }

        .option textarea {
            width: 100%;
            height: 30px;
        }

        .stateText {
            width: 100%;
            height: 60px;
        }

        .field {
            width: 100%;
            height: 120px;
        }

        .functionName {
            width: 50%;
        }

        .functionCall {
            border: 1px solid blue;
            margin-bottom: 10px;
        }

        .functionCallArg {
            width: 50%;
        }

        #definitionColumn {
            float: left;
        }
    </style>
</head>
<body>

<br>

<div id="definitionColumn">
    <div id="varColumn">

    </div>
    <div id="fnColumn">
    </div>
</div>
<textarea id="storyJson"></textarea>

<div id="storyRenderContainer">
    <div>
        <select id="storySelect"></select>
    </div>
    <div id="storyRender">
        test
    </div>

</div>


</body>
</html>