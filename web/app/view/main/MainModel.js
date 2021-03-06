/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('Chat.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: 'Chat',
        user: null,
        messageAreaHeight: {
            bind: 50,
            single: true
        },
        userNameWidth: 150
    },

    formulas: {
        title: function(get) {
            var user = get('user');
            var name = get('name');
            return Ext.isEmpty(user) ? name : name + ' [' + user + ']';
        }
    }
});