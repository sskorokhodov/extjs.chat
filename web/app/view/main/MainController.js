/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 */
Ext.define('Chat.view.main.MainController', {
    extend: 'Ext.app.ViewController',
    requires: [
        'Ext.window.Toast'
    ],
    alias: 'controller.main',

    init: function(application) {
        var user = Ext.String.htmlEncode(sessionStorage.getItem("userName"));
        if (Ext.isEmpty(user)) {
            this.askUserName();
        } else {
            this.setUser(user);
        }
        this.listenToMessages(this);
    },

    askUserName: function() {
        var that = this;
        Ext.Msg.prompt("Welcome!", "Enter user name:", function(btn, text) {
            if (btn == 'ok' && (/^[\d\w ]{1,64}$/).test(text)) {
                console.log('entered user name:', text);
                that.setUser(text);
            } else {
                that.askUserName();
            }
        });
    },

    setUser: function(user) {
        try {
            sessionStorage.setItem("userName", user);
        } catch (e) {
            console.log('can not save userName to sessionStorage', e);
        }
        this.getViewModel().set('user', user);
    },

    unsetUser: function() {
        sessionStorage.removeItem("userName");
        this.getViewModel().set('user', null);
    },

    listenToMessages: function(that) {
        Ext.Ajax.request({
            url: 'chat-log',
            method: 'GET',
            params: {
                limit: 100
            },
            success: function(response) {
                var ws = new WebSocket("ws://" + window.location.host + "/stream");
                that.replaceMessages(JSON.parse(response.responseText));
                ws.onopen = function(e) {
                    console.log("WS opened", ws.protocol);
                    that.onServerAvailable();
                };
                ws.onclose = function(e) {
                    that.onServerUnavailable();
                };
                ws.onmessage = function(e) {
                    console.log("new message: ", e.data);
                    var msg = JSON.parse(e.data);
                    that.appendMessages([{user: msg.user, text: msg.text}]);
                };
            },
            failure: function(response) {
                that.onServerUnavailable();
            }
        });
    },

    onServerUnavailable: function() {
        var that = this;
        var msgArea = that.lookupReference('messageArea');
        that.notifyWithToast('Error', 'Server is unavailable');
        msgArea.disable();
        setTimeout(function() {that.listenToMessages(that);}, 5000);
    },

    onServerAvailable: function() {
        var msgArea = this.lookupReference('messageArea');
        msgArea.enable();
    },

    notifyWithToast: function(title, message) {
        Ext.toast({
            title: title,
            html: message,
            width: 200,
            align: 'tr',
            animate: false
        });
    },

    appendMessages: function(messages) {
        this.updateMessages(messages, true);
    },

    replaceMessages: function(messages) {
        this.updateMessages(messages, false);
    },

    updateMessages: function(messages, shouldAppend) {
        var encodedMessages = messages.map(function(m) {
            var u = Ext.String.htmlEncode(m.user);
            var t = Ext.String.htmlEncode(m.text);
            return {user: u, text: t};
        });
        console.log(encodedMessages);
        var store = Ext.data.StoreManager.lookup('ChatLog');
        var chatArea = this.lookupReference('chatArea');
        store.loadRawData(encodedMessages, shouldAppend);
        chatArea.getView().scrollBy(0, 999999, true);
    },

    onSendBtnClick: function(btn, event) {
        this.sendMessage();
    },

    onMsgAreaKeyDown: function(target, e, eOpts) {
        if (e.getKey() === Ext.event.Event.RETURN && !e.shiftKey && Ext.isEmpty(target.getValue())) {
            e.stopEvent();
        }
    },

    onMsgAreaKeyPress: function(target, e, eOpts) {
        if (e.getKey() === Ext.event.Event.RETURN && !e.shiftKey) {
            this.sendMessage();
            e.stopEvent();
        }
    },

    sendMessage: function() {
        var that = this;
        var userName = that.getViewModel().data.user;
        var msgArea = that.lookupReference('messageArea');
        var message = msgArea.getValue();
        if (!Ext.isEmpty(message)) {
            msgArea.setValue('');
            console.log('sending message:', message);
            Ext.Ajax.request({
                url: '/send',
                method: 'POST',
                params: {
                    user: userName,
                    text: message
                },
                success: function(response) {
                    console.log('message sent');
                },
                failure: function(response) {
                    console.log('can\'t send message:', arguments);
                    that.notifyWithToast('Error', 'Can\'t send message');
                    msgArea.setActiveError('Can\'t send message');
                }
            });
        }
    },

    onLogoutBtnClick: function() {
        this.unsetUser();
        this.askUserName();
    }
});
