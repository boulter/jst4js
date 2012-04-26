/**
 * This function merges the properties on obj into this object.  It modifies this such that all
 * the properties on the given obj instance will be copied onto this.  If a property by the same
 * name already exists on this it will overwrite that property based on the given value of overwrite.
 *
 * @param obj properties of the object instance to merge into this.
 * @param overwrite whether or not to overwrite properties that already exist on this or not.
 *
 * @returns this.
 */
Object.prototype.merge = function(obj, overwrite) {
    for (var key in obj) {
        if (!obj.constructor.prototype[key] && (typeof this[key] == "undefined" || overwrite)) {
            this[key] = obj[key];
        }
    }
    return this;
};

/**
 * Returns an iterator over the properties on this object.
 */
Object.prototype.properties = function() {
    return Iterator(this._propertiesGenerator());
};

Object.prototype._propertiesGenerator = function() {
    for (var key in this) {
        if (this.hasOwnProperty(key)) {
            yield key;
        }
    }
};

/**
 * Takes the given String instance as a format pattern and returns a String resulting from applying the
 * given pattern to the given arguments of the function.  The pattern uses the {index} notation to refer
 * to the values of the arguments where index is a positive integer quantity starting with zero.
 */
String.prototype.format = function() {
    var expr = /{(\d+)}/gi;
    var index = 0;
    var result = "";
    var match = null;
    do {
        match = expr.exec(this);
        if (match) {
            result += this.substring(index, match.index) + arguments[ match[1] ];
            index = match.index + match[0].length;
        } else {
            result += this.substring(index);
        }
    } while (match);
    return result;
};

/**
 * Returns the XML escaped version of this string by encoding invalid characters
 * with XML safe characters.
 */
String.prototype.xml = function() {
    return StringUtil.escapeXml(this);
};

/**
 * Returns the HTML escaped version of this string by encoding invalid characters
 * with HTML safe characters.
 */
String.prototype.html = function() {
    return StringUtil.sanitize(StringUtil.escapeHtml(this));
};

/**
 * Returns a URL encoded string of this string by encoding characters so they are safe
 * to send in the path portion of a URL.
 */
String.prototype.encodeURI = function() {
    return encodeURI(this);
};

/**
 * Returns a new function where this equals scope, and the rest of the arguments are passed in order to this function.
 *
 * @param scope the value of the this parameter within the Function the delegate method is called on.
 */
Function.prototype.delegate = function(scope) {
    var _method = this;
    var args = Array.fromArguments(arguments);
    var scope = args.shift();
    return function() {
        _method.apply(scope, args);
    };
};

/**
 * Returns a new function where
 */
Function.prototype.curry = function() {
    if (!arguments.length) return this;
    var _method = this;
    var args = Array.fromArguments(arguments);
    return function() {
        return _method.apply(this, args.concat(Array.fromArguments(arguments)));
    };
};


if (!Array.prototype.reduce) {
    Array.prototype.reduce = function(reducer, initial) {
        var reduction = initial;
        for (var i = 0; i < this.length; i++) {
            reduction = reducer.call(this, reduction, this[i], i, this);
        }
        return reduction;
    };
}

/**
 * Use the members of this Array as parameters in the given template pattern supplied.
 * Template follows the format used in (@see String.format).
 *
 * @param template the format pattern to be used by the members of the array.
 *
 * @returns the string created by applying template + this array.
 */
Array.prototype.format = function(template) {
    return this.map(function(item) {
        return template.format(item);
    });
};

/**
 * Factory method that takes a Arguments object and converts it to an Array object.
 *
 * @param args Argument object that represents the arguments supplied to a function.
 *
 * @returns An Array consisting of the members of the given Arguments.
 */
Array.fromArguments = function(args) {
    return Array.prototype.slice.call(args);
};

/**
 * Gets a property from an object whether it be a Javascript or Java object.
 *
 * @param obj source to get this property on.
 * @param property the name of the property on this object to retrieve
 */
function getProperty(obj, property) {
    return obj[property] instanceof Function ? obj[property]() : obj[property];
}

/**
 * Returns a String formatted according to the given pattern.  The pattern follows
 * the syntax of java.text.SimpleDateFormat.
 *
 * @param pattern Format of returned date string.  Must conform to java.text.SimpleDateFormat syntax.
 */
Date.prototype.format = function( pattern ) {
    var f = new java.text.SimpleDateFormat( pattern );
    return f.format( this );
};

/**
 * Returns a String formatted according to the given pattern.  The pattern follows
 * the syntax of java.util.Formatter.
 *
 * @param pattern Format of the returned string.  Must conform to java.util.Formatter syntax.
 */
Number.format = function( pattern ) {
    return java.lang.String.format.apply( this, arguments );
};
