var rc = {
    ajax: function (callback) {
        var request = function () {
            if (typeof XMLHttpRequest !== undefined) {
                return new XMLHttpRequest;
            } else if (window.ActiveXObject) {
                var ieRequestTypes = ['MSXML2.XMLHttp.5.0', 'MSXML2.XMLHttp.4.0', 'MSXML2.XMLHttp.3.0', 'MSXML2.XMLHttp', 'Microsoft.XMLHttp'], xmlHttp;
                for (var i = 0; i < ieRequestTypes.length; i++) {
                    try {
                        xmlHttp = new ActiveXObject(ieRequestTypes[i]);
                        return xmlHttp;
                    } catch (e) {
                    }
                }
            }
        };
        var r = request();
        r.open('get', callback.url, true);
        r.send(null);
        r.onreadystatechange = function () {
            if (r.readyState === 4) {
                if (r.status === 200 || r.status === 0) {
                    callback.success.call(null, JSON.parse(r.responseText));
                } else {
                    callback.error.call(null, JSON.parse(r.responseText));
                }
            } else {
                //still processing
            }
        };
    }
};

var model = {
    posts: [],
    build_index: function (data) {
        var index = lunr(function () {
            this.ref("id");
            this.field("author");
            this.field("stage");
            this.field("tags");
            this.field("title", {boost: 10});
            this.field("markdown");
        });
        var posts = data.posts;
        for (var i = 0; i < posts.length; i++) {
            var post = posts[i];
            this.posts[i] = post;
            index.add(post);
        }
        return index;
    }
}

var view = {
    "search_menu": function() {
        return document.getElementById("search_menu");
    },

    "remove_search_results": function() {
        var menu = view.search_menu();
        if(menu) {
            menu.parentNode.removeChild(menu);
        }
    },
    "paint_results_under": function(element, results) {
        var menu = view.search_menu();
        if(!menu){
            menu = document.createElement("div");
            menu.className = "search_menu";
            menu.setAttribute("id","search_menu");
            element.parentNode.insertBefore(menu, element.nextSibling);
            document.onclick = function() { view.remove_search_results() };
            menu.onclick = function(event) { event.stopPropagation(); };
        } else {
            var children = menu.children;
            for (var i = 0; i < children.length; i++) {
                menu.removeChild(children[i]);
            }
        }

        for (var i = 0; i < results.length; i++) {
            var result = results[i];
            var menu_item = document.createElement("div");
            menu_item.className = "search_menu_item";
            menu_item.title = result.title;

            var link = document.createElement("a");
            link.setAttribute("href", result.link);
            link.appendChild(document.createTextNode(result.title));
            menu_item.appendChild(link);
            menu.appendChild(menu_item);
        }
    }
}

var events = {
    bind_search_to: function(id, index){
        var element = document.getElementById(id);
        element.onkeyup = function(event) {
            var query = element.value;
            if (query.length < 3) {
                view.remove_search_results();
            } else {
                var results = index.search(query);
                var reply = [];
                for (var i = 0; i < results.length; i++) {
                    var result = results[i];
                    reply[i] = model.posts[result.ref];
                }
                if(reply.length > 0) {
                    view.paint_results_under(element, reply);
                } else {
                    view.remove_search_results();
                }
            }
        }
    }
}

var search_initialize = function (id) {
    rc.ajax({
        "url": "posts.json",
        "success": function (data) {
            var index = model.build_index(data);
            events.bind_search_to(id, index);
        },
        "error": function (data) {
        }
    });

};
window.onload = function () {
    search_initialize("posts_search")
};