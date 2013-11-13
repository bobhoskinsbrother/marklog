<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${title!""}</title>
    <style type="text/css">
        body {
            font-family: sans-serif;
            font-size: 0.9em;
        }

        pre {
            padding: 10px;
            overflow: auto;
            color: white;
            background: #1d1f21;
            position: relative;
        }

        .article_info {
            background: #FAF0FB;
            color: #000000;
            padding: 20px;
        }
    </style>
</head>
<body>
${post}
<br/>

<div class="article_info">
<#if author?? && author?has_content><strong>Author</strong> ${author}<br/></#if>
<#if date??><strong>Date Posted</strong> ${date?string("dd MMM yyyy HH:mm z")}<br/></#if>
<#if tags?? && tags?size != 0><strong>Tags</strong> <#list tags as tag>${tag}<#if tag_has_next>, </#if></#list>
    <br/></#if>
</div>

</body>
</html>