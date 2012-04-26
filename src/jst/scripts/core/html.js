importPackage( Packages.jst );

function url( path, params, options ) {
    var result = [];

    if( path.charAt(0) == "@" ) {
        var ctx = servletContext.getContextPath();
        result.push( ctx );
        result.push( path.substring(1) );
    }

    if( params ) {
        result.push("?");
        var first = true;
        for each( var key in params.properties() ) {
            var value = params[key];
            if( !first ) {
                result.push( "&" );
            }
            result.push( key );
            result.push( "=" );
            result.push( value.encodeURI ? value.encodeURI() : value );
            first = false;
        }
    }
    return result.join('');
}

function app( url ) {
    var ctx = servletContext.getContextPath();
    return ctx.length > 0 ? ctx + url : url;
}

var Html = {
    tag : function( tagname, attrs, body ) {
        var t = [ "<", tagname ];
        for each( var key in attrs.properties() ) {
            t.push( ' ' );
            t.push( key );
            t.push( '="' );
            t.push( attrs[key] );
            t.push( '"');
        }
        if( body ) {
            t.push(">");
            if( body instanceof Array ) {
                t.push("\n");
                t.push( body.join("\n") );
                t.push("\n");
            } else if( body instanceof Function ) {
                t.push( body() );
            } else {
                t.push( body );
            }
            t.push("</");
            t.push(tagname);
            t.push(">");
        } else {
            t.push("/>");
        }

        return t.join("");
    },
    css : function( url, options ) {
        options = options || {};
        options.media = options.media || 'screen';
        options.href = url;
        options.rel = 'Stylesheet';
        options.type = 'text/css';

        return this.tag( 'link', options );
    },
    script : function( url, options ) {
        options = options || {};
        options.src = url;
        options.type = 'text/javascript';
        return this.tag('script', options, ' ' );
    },
    link : function( url, text, options ) {
        options = options || {};
        options.href = url;

        return this.tag('a', options, text );
    },
    html : function( value ) {
        return StringUtil.sanitize( value );
    }
};

var Form = {
    form : function( url, options, callback ) {
        options = options || {};
        options.action = url;
        options.method = options.method || "post";
        return Html.tag( "form", options, callback );
    },
    textfield : function( name, options ) {
        options = options || {};
        options.type = "text";
        options.name = name;
        return Html.tag( "input", options );
    },
    radio : function( name, value, checked, options ) {
        options = options || {};
        options.type = "radio";
        options.name = name;
        options.value = value;
        if( checked ) {
            options.checked = "checked";
        }
        return Html.tag( "input", options );
    },
    checkbox : function( name, value, checked, options ) {
        options = options || {};
        options.type = "checkbox";
        options.name = name;
        options.value = value;
        if( checked ) {
            options.checked = "checked";
        }
        return Html.tag( "input", options );
    },
    button : function( name, value, options ) {
        options = options || {};
        options.type = "button";
        options.name = name;
        options.value = value;
        return Html.tag( "input", options );
    },
    submit : function( name, value, options ) {
        options = options || {};
        options.type = "submit";
        options.name = name;
        options.value = value;
        return Html.tag( "input", options );
    },
    select : function( name, keys, select, options ) {
        options = options || {};
        options.name = name;

        var lblField = options.label || "text";
        var valField = options.value  || "value";
        delete options.label;
        delete options.value;

        var opts = [];
        for( var index = 0; index < keys.length; index++ ) {
            var item = keys[index];
            if( item instanceof Object || item instanceof java.lang.Object ) {
                var label = getProperty( item, lblField );
                var value = getProperty( item, valField );
                if( select != null && (select == value || select == index) ) {
                    opts.push( Html.tag("option", { value: value, selected: "selected" }, label ) );
                } else {
                    opts.push( Html.tag("option", { value: value }, label ) );
                }
            } else {
                if( select != null && (select == value || select == index) ) {
                    opts.push( Html.tag("option", { value: item, selected: "selected" }, item ) );
                } else {
                    opts.push( Html.tag("option", {value: item}, item ) );
                }
            }
        }

        return Html.tag( "select", options, opts );
    },
    textarea : function( name, rows, cols, options ) {
        options = options || {};
        options.name = name;
        options.rows = rows;
        options.cols = cols;
        return Html.tag( "textarea", options );
    },
    password : function( name, options ) {
        options = options || {};
        options.type = "password";
        options.name = name;
        return Html.tag( "input", options );
    },
    fileUpload : function( name, options ) {
        options = options || {};
        options.name = name;
        options.type = "file";
        
        return Html.tag("input", options );
    },
    hidden : function( name, value, options ) {
        options = options || {};
        options.name = name;
        options.type = "hidden";
        if( value ) {
            options.value = value;
        }

        return Html.tag("input", options );
    },
    imageButton : function( name, src, options ) {
        options = options || {};
        options.name = name;
        options.src = src;
        options.type = "image";
        return Html.tag( "input", options );
    }
};
