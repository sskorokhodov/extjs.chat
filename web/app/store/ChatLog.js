Ext.define('Chat.store.ChatLog', {
    extend: 'Ext.data.Store',
    model: 'Chat.model.ChatMsg',
    storeId: 'chatLog',
    data : []
});