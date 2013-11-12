<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${title!""}</title>
    <style type="text/css">
        body {
            font-family: ubuntu;
            font-size: 0.8em;
        }
    </style>
</head>
<body>
${post}

<#if author?? && author?has_content>Author: ${author}<br/></#if>
<#if date??>Date Posted: ${date?string("dd MMM yyyy HH:mm z")}<br/></#if>
<#if tags?? && tags?size != 0>Tags: <#list tags as tag>${tag}<#if tag_has_next>, </#if></#list><br/></#if>

</body>
</html>