<#-- @ftlvariable name="coven" type="com.liquidenthusiasm.domain.Coven" -->
<#-- @ftlvariable name="person" type="com.liquidenthusiasm.domain.Person" -->
<#-- @ftlvariable name="action" type="com.liquidenthusiasm.action.AbstractAction" -->
<#-- @ftlvariable name="view" type="com.liquidenthusiasm.domain.StoryView" -->
<#-- @ftlvariable name="state" type="com.liquidenthusiasm.domain.StoryInstance" -->
<#-- @ftlvariable name="actionList" type="java.util.List<com.liquidenthusiasm.action.AbstractAction>" -->
<#include "macro/storyOptionField.ftl"/>
<#include "macro/storyRequirements.ftl"/>
<#include "macro/travelLink.ftl"/>

<#if view??>
<div class="storyView">
    <#if view.flash?? >
        <div class="flash">
            <i>${view.flash?replace('\n', '<br>')}</i>
        </div>
    </#if>
    <#if view.valueChanges?has_content >
        <div class="valueChange">
            <#list view.valueChanges as valueChange>
            <#-- @ftlvariable name="valueChange" type="com.liquidenthusiasm.domain.ValueChange" -->
                <#if !valueChange.adminOnly || coven.admin >
                    <div <#if valueChange.adminOnly >class="adminOnly"</#if> >
                        <#if valueChange.oldValue?? && valueChange.newValue?? && valueChange.newValue != "0" >
                            <#if valueChange.oldValue == valueChange.newValue >
                                <div class="valueChange">${valueChange.var} is still "${valueChange.newValue}".</div>
                            <#else>
                                <div class="valueChange">${valueChange.var} went from "${valueChange.oldValue}" to
                                    "${valueChange.newValue}"!
                                </div>
                            </#if>
                        <#elseif valueChange.newValue??>
                            <div class="valueChange">${valueChange.var} is now "${valueChange.newValue}"!</div>
                        <#elseif valueChange.oldValue?? || valueChange.oldValue == "0">
                            <div class="valueChange">${valueChange.var} is now gone!</div>
                        </#if>
                    </div>
                </#if>
            </#list>
        </div>
    </#if>
    <b>${view.heading}</b><br/>
${view.storyText?replace('\n', '<br>')}
    <hr>
    <#if view.options?? >
        <#list view.options as option>
            <#if option.isViewable(coven, person, state) >
                <div class="option<#if !option.isEnabled(coven, person, state)> disabled</#if>" id="storyOptionContainer${option_index}">
                    <table>
                        <tr>
                            <td width="100%">
                                <b>${option.heading}</b><br/>
                            ${option.getHtmlText(coven, person, state)?replace('\n', '<br>')}
                            </td>
                            <td valign="bottom">
                                <#if option.timeCost gt person.timeRemaining>
                                    Not&nbsp;enough&nbsp;time:&nbsp;(${option.timeCost}&nbsp;hours)
                                <#elseif option.isEnabled(coven, person, state)>
                                    <a href="javascript:chooseActionOption(${action.actionId}, ${option.id})">
                                        Go!<#if option.timeCost gt 0>&nbsp;(${option.timeCost}&nbsp;hours)</#if>
                                    </a>
                                <#else>
                                    Requirements not met.
                                </#if>

                                <#if option.viewTriggers?has_content>
                                    <@storyRequirements coven=coven person=person state=state triggerList=option.viewTriggers/>
                                </#if>
                                <#if option.viewTriggers?has_content && option.enableTriggers?has_content>
                                    <div>---</div>
                                </#if>
                                <#if option.enableTriggers?has_content>
                                    <@storyRequirements coven=coven person=person state=state triggerList=option.enableTriggers/>
                                </#if>
                            </td>
                        </tr>
                    </table>
                <#--<#if option.fields?? >-->
                <#--<table class="form">-->
                <#--<#list option.fields as field >-->
                <#--<@storyOptionField field=field optionIndex=option_index/>-->
                <#--</#list>-->
                <#--</table>-->
                <#--</#if>-->
                </div>
            </#if>
        </#list>
    </#if>
    <#if view.canCancel!true || !(view.options??) || view.options?size == 0>
        <div class="cancel option">
            <a href="javascript:chooseActionOption(${action.actionId}, -1)">
                <#if view.options?? >On second thought...<#else>Continue</#if>
            </a>
        </div>
    </#if>
</div>
<#else>
<div class="actionList">
    <div id="travel">
        Focusing on:
        <select>
            <#list coven.members as member>
                <option value="${member.id}">${member.name}</option>
            </#list>
        </select>
        Travel to:
        <#if person.parentCategory??>
        <ul>
        <li><@travelLink category=person.parentCategory/>
        </#if>
        <ul>
            <#list person.siblingCategories as siblingCategory>
                <#if person.currentCategory == siblingCategory>
                    <li>${person.currentCategory.label}
                        <#if person.childCategories?has_content>
                            <ul>
                                <#list person.childCategories as childCategory>
                                    <li><@travelLink category=childCategory/></li>
                                </#list>
                            </ul>
                        </#if>
                    </li>
                <#else>
                    <li><@travelLink category=siblingCategory/></li>
                </#if>
            </#list>
        </ul>
        <#if person.parentCategory??>
        </li>
        </ul>
        </#if>
    </div>

    <#if actionList?has_content>
        <#list actionList as action>
        <#-- @ftlvariable name="action" type="com.liquidenthusiasm.action.AbstractAction" -->
            <div class="action">
                <b><a href="javascript:performAction(${action.actionId})">${action.actionName}</a></b><#if action.actionDescription??>
                : ${action.actionDescription}</#if>
            </div>
        </#list>
    <#else>
        <div class="noActions">Looks like there's nothing to do here...</div>
    </#if>
</div>
</#if>