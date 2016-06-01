<#-- @ftlvariable name="errors" type="java.lang.String" -->

<html>
<body>
<h2>Create an account</h2>

<#if errors??>
<div class="error">
${errors}
</div>
</#if>
<form method="POST">
    <label>Email: </label><input type="email" name="email" value="${email}"/><br/>
    <label>Community name: </label><input type="text" name="displayName" value="${displayName}"/><br/>
    <label>Password: </label><input type="password" name="pass"/><br/>
    <label>Password again: </label><input type="password" name="passAgain"/><br/>
    <input type="submit" value="Create account"/>
</form>
</body>
</html>