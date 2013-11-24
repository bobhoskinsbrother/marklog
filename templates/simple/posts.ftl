<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${title!""}</title>
    <link rel="stylesheet" href="/simple/general.css" type="text/css"/>
    <script type="text/javascript" src="/simple/js/lunr.min.js"></script>
    <script type="text/javascript" src="/simple/js/search.js"></script>
</head>
<body>
<div class="main_area">
    <div class="posts">
    <#list posts as post>
        <#include "_post.ftl" />
        <#if post_has_next>
            <hr/>
        </#if>
    </#list>
    </div>
<#if (tags_links?? && tags_links?size > 0) || (archives_links?? && archives_links?size > 0)>
    <div class="nav">
        <input type="search" placeholder="Search" id="posts_search" class="search_field"/>

        <#if (tags_links?? && tags_links?size > 0)>
        <h3>Tags</h3>
            <#list tags_links as link>
                <a href="${link.location}">${link.text}</a>
                <#if link_has_next>
                <br/>
                </#if>
            </#list>
        </#if>

        <#if (archives_links?? && archives_links?size > 0)>
        <h3>Archives</h3>
            <#list archives_links as link>
                <a href="${link.location}">${link.text}</a>
                <#if link_has_next>
                <br/>
                </#if>
            </#list>
        </#if>

    </div>
</#if>
</div>
</body>
</html>
