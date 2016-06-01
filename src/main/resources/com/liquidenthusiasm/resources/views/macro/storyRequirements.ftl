<#-- @ftlvariable name="coven" type="com.liquidenthusiasm.domain.Coven" -->
<#-- @ftlvariable name="person" type="com.liquidenthusiasm.domain.Person" -->
<#-- @ftlvariable name="state" type="com.liquidenthusiasm.domain.StoryInstance" -->
<#-- @ftlvariable name="option" type="com.liquidenthusiasm.action.story.StoryOption" -->
<#-- @ftlvariable name="trigger" type="com.liquidenthusiasm.action.condition.StoryTrigger" -->

<#macro storyTrigger coven person state trigger>
    <#list trigger.conditions as condition>
    <img class="<#if !condition.isTriggered(coven, person, state)>notTriggered </#if>storyCondition"
         src="${condition.getImage(coven, person, state)?html}"
         title="${condition.getDescription(coven, person, state)?html}"
            />
    </#list>
</#macro>

<#macro storyRequirements triggerList coven person state>
    <#if triggerList??>
    <div class="storyRequirements">
        <div class="storyConditionSet">
            <#list triggerList as trigger>
                <@storyTrigger coven=coven person=person state=state trigger=trigger /> <br/>
            </#list>
        </div>
    </div>
    </#if>
</#macro>