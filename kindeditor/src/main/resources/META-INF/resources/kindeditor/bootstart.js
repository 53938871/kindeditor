var Sniper = Sniper || {};
Sniper.NS = function(name) {
    var parts = name.split('.');
    var current = Sniper;
    for(var i in parts) {
        if(!current[parts[i]]) {
            current[parts[i]] = {};
        }
        current = current[parts[i]];
    }
}


Sniper.NS('KindEditor');
Sniper.KindEditor.init = function(formName,textAreaName) {
    KindEditor.ready(function(K) {
        var editor1 = K.create('textarea[name=' + textAreaName + ']', {
            cssPath : '/kindeditor/plugins/code/prettify.css', //防止出现相对路径的404错误
            uploadJson : 'ke/upload',
            fileManagerJson : 'ke/fileManager',
            allowFileManager : true,
            afterCreate : function() {
                var self = this;
                K.ctrl(document, 13, function() {
                    self.sync();
                    document.forms[formName].submit();
                });
                K.ctrl(self.edit.doc, 13, function() {
                    self.sync();
                    document.forms[formName].submit();
                });
            }
        });
        prettyPrint();
    });
}

Sniper.KindEditor.simple = function(textAreaName) {
    var editor;
    KindEditor.ready(function(K) {
        editor = K.create('textarea[name="' + textAreaName +'"]', {
            resizeType : 1,
            allowPreviewEmoticons : false,
            allowImageUpload : false,
            items : [
                'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
                'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
                'insertunorderedlist', '|', 'emoticons', 'image', 'link']
        });
    });
}
