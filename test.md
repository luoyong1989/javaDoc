哈哈
===

大幅度发快递费
---

## 哈哈


-----
		
	@Bean
	public TaskScheduler threadPoolTaskScheduler() {
	    ThreadPoolTaskScheduler poolTaskScheduler = new ThreadPoolTaskScheduler();
	    poolTaskScheduler.setPoolSize(10);
	    return new ThreadPoolTaskScheduler();
	}
	
	


=======


###下一个

	 protected Proxy proxy;
	    protected String url;
	    protected int timeout = 20000;
	    public boolean debug;
	    public String requestDump;
	    public String responseDump;
	    private String xmlVersionTag = "";
	    protected static final String CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8";
	    protected static final String CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8";
	    protected static final String USER_AGENT = "ksoap2-android/2.6.0+";
	    private int bufferLength = 262144;
	
	    private HashMap prefixes = new HashMap();
	
	    public HashMap getPrefixes() {
	        return this.prefixes;
	    }
	
	    public Transport() {
	    }
	
	    public Transport(String url) {
	        this(null, url);
	    }
	
	    public Transport(String url, int timeout) {
	        this.url = url;
	        this.timeout = timeout;
    }