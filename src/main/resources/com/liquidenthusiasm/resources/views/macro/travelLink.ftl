<#-- @ftlvariable name="category" type="com.liquidenthusiasm.action.ActionCategory" -->

<#macro travelLink category>
<a href="javascript:travelTo(${category.id})">${category.label}</a>
</#macro>