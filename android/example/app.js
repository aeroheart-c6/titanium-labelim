// This is a test harness for your module
// You should do something interesting in this harness
// to test out the module and to provide instructions
// to users on how to use it by example.

var Labelim = require('com.aeroheart.ti.labelim');

// open a single window
var win = Ti.UI.createWindow({
    backgroundColor:'white'
});
win.open();

if (Titanium.Platform.name == "android") {
    var label = Labelim.createLabel({
        maxLines          : 2,
        ellipsize         : true,
        scrollHorizontally: false,
        text              : ''
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
            .concat('The quick brown fox jumps over the lazy dog')
    });
    
    win.add(label);
}
