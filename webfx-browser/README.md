WebFX
=====
Same goal as the original WebFX project: to practice/learn JavaFX. 
With improved UI, more HTML - FXML convergence, and chrome:// URLs, it's more like a modern browser.  

Running this WebFX browser
=====
1. Get the code
2. Open in NetBeans
3. Click 'Run'
4. Go to the following URL: http://attila.hontvari.net/data/webfx-samples/

This URL is a pure, static HTML rendered by JavaFX's WebView, with links to sample applications that are pure FXML+CSS+JavaScript.
You may also want to go to a HTML5 website, such as this one from Mozilla HTML5 Showcase:

- http://html5demos.com/canvas

Resource Bundles
=====
Supports loading resource bundles from the Web Server hosting the FXML pages. Convetion is having the .properties with the same name as the FXML page.
*Example*
- http://www.mysite.com/login.fxml
- http://www.mysite.com/login.properties

Developer can also offer language/country specifics, i.e. login_pt_BR.properties

chrome:// URLs
=====
Similar to Google Chrome's or FireFox's "chrome" URLs, it's goal is to contain/serve browser resources. 
Currently there are only 2 chrome URLs:
- chrome://about
- chrome://newtab

Security Layer (planned)
=====
The security layer must provide a sandbox on each tab, to run JavaFX pages. The sandbox must ensure that:
- unsecure code will be run (i.e. local access to files, System.exit, network, etc)
- dialogs/windows can't be created, unless the user gives permition
- access to parent objects (the Tab object, for example)
- provide management and control for long running process, memory consumption, etc.

WFX Protocol (also planned)
=====
There should be an specific protocol to allow faster, easier server-client communication. 
It is already possible though, to use HTTP.
