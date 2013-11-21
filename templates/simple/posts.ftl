<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${title!""}</title>
    <link rel="stylesheet" href="simple/general.css" type="text/css"/>
</head>
<body>
<div class="posts">
<#list posts as post>
<#include "_post.ftl" />
<#if post_has_next>
<hr/>
</#if>
</#list>
</div>
</body>
</html>