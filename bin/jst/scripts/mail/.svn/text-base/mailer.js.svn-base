function Mailer( from, username, password, properties ) {
    var props = new java.util.Properties();
    for( var key in properties.properties() ) {
        props.setProperty( key.toString(), properties[key].toString() );
    }
    this.emailer = new Packages.jst.mail.Emailer( props, from, username, password );
}

Mailer.prototype = {
    send: function( to, subject, template, htmlTemplate, params, attachments ) {
        params = params || {};
        attachments = attachments || {};

        var email = this.emailer.email( to, subject, template, htmlTemplate );
        if( params ) {
            for each( var key in params.properties() ) {
                email.bind( key, params[key] );
            }
        }

        if( attachments ) email.attach( attachments );
        email.send();
    }
};
