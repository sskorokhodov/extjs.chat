Ext.define('Chat.model.ChatMsg', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'user', type: 'string'},
        {name: 'text', type: 'string'}
    ]
});