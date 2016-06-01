<#-- @ftlvariable name="coven" type="com.liquidenthusiasm.domain.Coven" -->
<#-- @ftlvariable name="person" type="com.liquidenthusiasm.domain.Person" -->
<#-- @ftlvariable name="action" type="com.liquidenthusiasm.action.AbstractAction" -->
<#-- @ftlvariable name="view" type="com.liquidenthusiasm.domain.StoryView" -->
<#-- @ftlvariable name="state" type="com.liquidenthusiasm.domain.StoryInstance" -->
<#-- @ftlvariable name="field" type="com.liquidenthusiasm.action.story.FieldDef" -->
<#macro storyOptionField optionIndex field>
<tr>
    <td>${field.label}</td>
    <td>
        <#if field.type == "text">
            <input type="text" value="${field.defaultValue}" name="${field.name}" class="optionField${optionIndex}"/>
        <#elseif field.type=="select">
            <select>
                <#list field.options as selectOption>
                    <option value="${selectOption.value}">${selectOption.label}</option>
                </#list>
            </select>
        <#else>
            Unknown field type: ${field.type}
        </#if>
    </td>
</tr>
</#macro>