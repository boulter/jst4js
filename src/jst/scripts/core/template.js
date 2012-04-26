function Template( templateFunction, templateObject ) {
    this.__template = templateFunction;
    this.__templateObject = templateObject;
}

Template.prototype = {
    evaluate : function() {
        this.__collectedContent = {};
        this.__scripts = {};
        this.__styles = {};
        this.__output = [];
        this.contentForLayout = this.__template.apply( this, Array.fromArguments(arguments) );
        if( this.__layout ) {
            logger.info( this.__templateObject.getURL() + " rendering with layout " + this.__layout.__templateObject.getURL() );
            this.__layout.__scripts = this.__scripts;
            this.__layout.__styles = this.__styles;
            return this.__layout.__template.call( this );
        } else {
            logger.info( this.__templateObject.getURL() + " rendering without a layout." );
            return this.contentForLayout;
        }
    },
    collectContentFor : function( name, callback ) {
        if( !this.__collectedContent[name] ) this.__collectedContent[name] = [];
        this.__collectedContent[name].push( callback );

        if( !this[name] ) {
            this[name] = function() {
                var output = [];
                var content = this.__collectedContent[name];
                for( var i = 0; i < content.length; i++ ) {
                    var current = content[i];
                    if( current instanceof Function ) {
                        logger.info( "Calling collected function.");
                        output.push( current.call(this) );
                    } else {
                        logger.info( "Calling collected value.");
                        output.push( current );
                    }
                }
                return output.join('');
            }
        }
    },
    render : function( partial, options ) {
        var javaTemplateObject = runtime.read( partial );
        var template = new Template( eval( '' + javaTemplateObject.getName() ), javaTemplateObject );
        var formalParams = template.getFormalParameters();
        var actualParams = [];
        for each( var p in Iterator(formalParams) ) {
            actualParams.push( options[p] );
        }
        var result = template.evaluate.apply( template, actualParams );
        this.__scripts.merge( template.__scripts );
        this.__styles.merge( template.__styles );
        return result;
    },
    getFormalParameters: function() {
      return this.__templateObject.getFormalParameters();  
    },
    include : function( jsScript ) {
        runtime.include( jsScript );
    },
    layout : function( layout ) {
        var javaTemplateObject = runtime.read( layout );
        this.__layout = new Template( eval( '' + javaTemplateObject.getName() ), javaTemplateObject );
    },
    scripts : function( script ) {
        if( script ) {
            if( !this.__scripts[script] ) {
                this.__scripts[script] = script;
            }
        } else {
            var output = [];
            for each( var key in this.__scripts.properties() ) {
                output.push( Html.script(this.__scripts[key]) );
            }
            return output.join("\n");
        }
    },
    styles : function( style ) {
        if( style ) {
            if( !this.__styles[style] ) {
                this.__styles[style] = style;
            }
        } else {
            var output = [];
            for each( var key in this.__styles.properties() ) {
                output.push( Html.css(this.__styles[key]) );
            }
            return output.join("\n");
        }
    }
};

