# WebView Auto Login - Android

Auto Login System based on WebView and Generic LoginService

# Current Services!

  - Google.com Account automated connection
  - Hotels.com automated connection

You can also:
  - Create any automated login service
  - Inject any JavaScript code inside WebViews
  
### Installation

Install the dependency with Gradle inside your Main Application

    dependencies {
        implementation project(":app")
    }

### How to use it

Here's what you need to implement, to have an automated LoginService

    // Context should be your Main Activity of your application
    LoginService loginService = new GoogleService(context);
    // Email and Password should be clear string
    loginService.autoLogin(<email>, <password>, new ResponseCallback() {
        @Override
        public void getResponse(ResponseEnum responseEnum, String data) {
            if (responseEnum != ResponseEnum.SUCCESS) 
                // You can use responseEnum.getName() to get what went wrong and print it
            else 
                // User automatically authenticated
        }
    });

### Create an automated LoginService

We have here created a LoginService instance, creating thus an automated login for a specified website.

    public class MyTestService extends LoginService {

        /**
         *
         * Step Enum can be ONE_STEP or TWO_STEP login
         * ONE_STEP is when email and password input are on the same page
         * TWO_STEP is when user have to click on next button
         *
         * Delay is how much time the bot should wait between entering inputs and clicking on buttons
         *
         * @param context Main Activity Context
         */
        public MyTestService(final Context context) {
            super(StepEnum.TWO_STEP, context, "https://mytestservice.com/login", 2);
        }
    
        /**
         * We are here defining which method to use inside JavaScript code injector
         * and the identifier that we should use to find the class to fill input or to click on
         */
        @Override
        void setupElements() {
            /*
                We are here selecting javascript getElementById method with email-input identifier
                Resulting in: document.getElementById('email-input');
             */
            this.step.getStep().addElement(TypesAuthElement.USERNAME, new ElementValue(
                    "getElementById", "email-input", false
            ));
    
             /*
                We are here selecting javascript getElementsByName method with password identifier
                Because getElementsByName return and array we thus choose the first one
                Resulting in: document.getElementsByName('password')[0];
             */
            this.step.getStep().addElement(TypesAuthElement.PASSWORD, new ElementValue(
                    "getElementsByName", "password", true
            ));
    
             /*
                We are here selecting javascript querySelectorAll method with button_next_login identifier
                Because querySelectorAll return and array we thus choose the first one
                This action will be automatically a click action because of BUTTON type
                Resulting in: document.querySelectorAll('button_next_login')[0].click();
             */
            this.step.getStep().addElement(TypesAuthElement.BUTTON_LOGIN, new ElementValue(
                    "querySelectorAll", "button_next_login", true
            ));
    
             /*
                We are here selecting javascript querySelectorAll method with button_password identifier
                This action will be automatically a click action because of BUTTON type
                Resulting in: document.getElementById('button_password').click();
             */
            this.step.getStep().addElement(TypesAuthElement.BUTTON_PASSWORD, new ElementValue(
                    "getElementById", "button_password", false
            ));
        }
    
        /**
         *
         * We are here choosing how to handle Response with JavaScript code checker
         * 
         * @return ResponseEnum and JavaScript Linked Code
         */
        @Override
        protected HashMap<ResponseEnum, String> setupTests() {
            return new HashMap<ResponseEnum, String>(){{
                // Here we are checking if class 'bad_email_icon' exists, if it does response will return BAD_EMAIL
                put(ResponseEnum.BAD_EMAIL, "document.getElementsByClassName('bad_email_icon')[0]");
                
                // Here we are checking the query 'bad_password span' exists, if it does response will return BAD_PASSWORD
                put(ResponseEnum.BAD_PASSWORD, "document.querySelector('.bad_password span')");
                
                // Here we are checking if class 'confirmation_email' exists, if it does, it means that user need to confirm his identity
                // Phone Number, email... Response will me set to CONFIRMATION_IDENTITY
                put(ResponseEnum.CONFIRMATION_IDENTITY, "document.getElementsByClassName('confirmation_email')[0]");
                
                // Here we are checking if id 'captcha' exists and if the user has to validate a captcha, response will be CAPTCHA
                put(ResponseEnum.CAPTCHA, "document.getElementById('captcha')[0]");
                
                // Finally, we are trying here to identify an element that only logged user can see meaning that user is authenticated
                // Response will be set to SUCCESS
                put(ResponseEnum.SUCCESS, "document.getElementsByClassName('email')[0]");
            }};
        }
    }

### How to inject JavaScript code

We are here creating a WebView and injecting our JavaScript inside.

    // We are instantiating our main WebViewCreator Wrapper with our default settings
    WebViewCreator webViewCreator = new WebViewCreator(this, new HashMap<Settings, Boolean>(){{
        put(Settings.JavaScript, true);
        put(Settings.DomStorage, true);
        put(Settings.ClearCache, true);
    }});

    // We thus initialise our WebInjector, with our WebViewCreator instance and url that we want to inject code in
    WebInjector webInjector = new WebInjector(webViewCreator, "https://google.com");
    
    // We are here creating the WebView and loading url
    webInjector.load();
    
    // Finally we inject our code, the callback will be called only when the WebPage is fully loaded
    webInjector.inject("document.getElementsById('title')[0]", new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String s) {
            // S is JavaScript response
        }
    });
    
You can also inject JavaScript inside a running webview, but we recommend to use our WebViewCreator

    WebInjector webInjector = new WebInjector(null, "https://google.com");

    webInjector.injectJavascript("document.getElementsById('title')[0]", <your_webview>, new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String s) {
            // S is JavaScript response
        }
    });

Contributors
----
- [Jordan Bonaldi (Back End API Solution)](http://github.com/jordanbonaldi/)
- [Lucas Benkemoun (Front End Solution)](https://github.com/LeBenki)

License
----

MIT
