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
<div class="blog_header">
    <div class="blog_header_inner">
        <span><a href="/">${blog_title!""}</a></span>
        <span class="blog_header_links">
            <#list header_links as link><a href="${link.location}">${link.text}</a><#if link_has_next> | </#if></#list>
        </span>
    </div>
</div>

<div class="main_area">
    <div class="posts">
    <#list posts as post>
        <#assign header = post.header/>

        <div class="header">
            <#if header.title?? && header.title?has_content>
                <h1><a href="${link_resolver.resolve(post)}">${convert.toHtml(header.title)}</a></h1>
            </#if>
            <#if header.author?? && header.author?has_content><p class="a_bit_smaller">by ${header.author}<#if header.date??>, ${header.date?string("dd MMM yyyy")}</#if></p></#if>
        </div>

        <div class="post">
        ${convert.toHtml(post.markdown)}
        </div>

        <div class="post_info">
            <#if header.date??><strong>At</strong> ${header.date?string("HH:mm")}<br/></#if>
            <#if header.tags?? && header.tags?size != 0>
                <strong>Tags</strong> <#list header.tags as tag>${tag}<#if tag_has_next>, </#if></#list></#if>
        </div>

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
