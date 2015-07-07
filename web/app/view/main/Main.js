/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 */
Ext.define('Chat.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: [
        'Chat.view.main.MainController',
        'Chat.view.main.MainModel'
    ],
    xtype: 'app-main',
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    layout: {
        type: 'vbox',
        pack: 'start',
        align: 'stretch'
    },
    items: [{
        xtype: 'grid',
        name: 'chatArea',
        reference: 'chatArea',
        bind: {
            title: '{title}',
        },
        editable: false,
        hideHeaders: true,
        disableSelection: true,
        viewConfig: {
            preserveScrollOnReload: true
        },
        flex: 1,
        store: 'ChatLog',
        tools: [{
            type: 'gear',
            callback: 'onLogoutBtnClick',
            tooltip: 'Logout'
        }],
        columns: [{
            text: 'User',
            dataIndex: 'user',
            width: 150,
            renderer: function(val, meta) {
                meta.tdStyle = 'background-color:#e0eaf3; font-weight:bold;';
                return val;
            }
        }, {
            text: 'Message',
            dataIndex: 'text',
            flex: 1,
            renderer: function(val) {
                return '<div style="white-space:pre-wrap; word-wrap:break-word;">'+ val +'</div>';
            }
        }]
    }, {
        xtype: 'container',
        height: 50,
        layout: {
            type: 'hbox',
            pack: 'start',
            align: 'stretch'
        },
        items: [{
            xtype: 'textarea',
            reference: 'messageArea',
            grow: true,
            flex: 1,
            anchor: '100%',
            emptyText: "Enter message...",
            enableKeyEvents: true,
            listeners: {
                keypress: 'onMsgAreaKeyPress',
                keydown: 'onMsgAreaKeyDown'
            }
        }, {
            xtype: 'button',
            text: 'Send',
            handler: 'onSendBtnClick'
        }]
    }]
});
