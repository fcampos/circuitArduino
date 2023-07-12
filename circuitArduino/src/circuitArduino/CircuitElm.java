package circuitArduino;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import circuitArduino.UI.EditInfo;
import circuitArduino.components.inout.RailElm;
import circuitArduino.components.inout.SweepElm;
import circuitArduino.components.inout.VoltageElm;
import circuitArduino.CirSim;
import circuitArduino.CircuitElm;
import circuitArduino.Editable;
import matlabcontrol.MatlabInvocationException;
//import org.jfree.graphics2d.svg.SVGGraphics2D; 
//import org.jfree.graphics2d.svg.SVGUtils;
public abstract class CircuitElm  implements Editable{
    public static double voltageRange = 5;
    static int colorScaleCount = 32;
    static Color colorScale[];
    static double currentMult, powerMult;
    protected static Point ps1;
	protected static Point ps2;
    public static CirSim sim;
    public static Color whiteColor;
	protected static Color selectColor;
	protected static Color lightGrayColor;
    protected static Font unitsFont;

    public static NumberFormat showFormat, shortFormat, noCommaFormat;
    protected static final double pi = 3.14159265358979323846;

    protected int x;
	public int y;
	public int x2;
	public int y2;
	protected int flags;
	public int nodes[];
	protected int voltSource;
    protected int dx;
	protected int dy;
	protected int dsign;
    protected double dn;
	double dpx1;
	double dpy1;
    protected Point point1;
	protected Point point2;
	protected Point lead1;
	protected Point lead2;
    public double volts[];
    protected double current;
	protected double curcount;
    protected Rectangle boundingBox;
    protected boolean noDiagonal;
    public boolean selected;
    protected int getDumpType() { return 0; }
    protected Class getDumpClass() { return getClass(); }
    protected int getDefaultFlags() { return 0; }

    static void initClass(CirSim s) {
	unitsFont = new Font("SansSerif", 0, 10);
	sim = s;
	
	colorScale = new Color[colorScaleCount];
	int i;
	for (i = 0; i != colorScaleCount; i++) {
	    double v = i*2./colorScaleCount - 1;
	    if (v < 0) {
		int n1 = (int) (128*-v)+127;
		int n2 = (int) (127*(1+v));
		colorScale[i] = new Color(n1, n2, n2);
	    } else {
		int n1 = (int) (128*v)+127;
		int n2 = (int) (127*(1-v));
		colorScale[i] = new Color(n2, n1, n2);
	    }
	}
	
	ps1 = new Point();
	ps2 = new Point();

	showFormat = DecimalFormat.getInstance();
	showFormat.setMaximumFractionDigits(2);
	shortFormat = DecimalFormat.getInstance();
	shortFormat.setMaximumFractionDigits(1);
	noCommaFormat = DecimalFormat.getInstance();
	noCommaFormat.setMaximumFractionDigits(10);
	noCommaFormat.setGroupingUsed(false);
    }
    
    protected CircuitElm(int xx, int yy) {
	x = x2 = xx;
	y = y2 = yy;
	flags = getDefaultFlags();
	allocNodes();
	initBoundingBox();
    }
    protected CircuitElm(int xa, int ya, int xb, int yb, int f) {
	x = xa; y = ya; x2 = xb; y2 = yb; flags = f;
	allocNodes();
	initBoundingBox();
    }
    
    protected void initBoundingBox() {
	boundingBox = new Rectangle();
	boundingBox.setBounds(min(x, x2), min(y, y2),
			      abs(x2-x)+1, abs(y2-y)+1);
    }
    
    protected void allocNodes() {
	nodes = new int[getPostCount()+getInternalNodeCount()];
	volts = new double[getPostCount()+getInternalNodeCount()];
    }
    public String p_dump() {return dump();}
    protected String dump() {
	int t = getDumpType();
	return (t < 127 ? ((char)t)+" " : t+" ") + x + " " + y + " " +
	    x2 + " " + y2 + " " + flags;
    }
    protected void reset() {
	int i;
	for (i = 0; i != getPostCount()+getInternalNodeCount(); i++)
	    volts[i] = 0;
	curcount = 0;
    }
    protected void draw(Graphics g) {}
    protected void setCurrent(int x, double c) { current = c; }
    public double getCurrent() { return current; }
    protected void doStep() {}
    protected void delete() {}
   // public void p_delete() {delete();}
    protected void startIteration() {}
    public double getPostVoltage(int x) { return volts[x]; }
    protected void setNodeVoltage(int n, double c) {
	volts[n] = c;
	calculateCurrent();
    }
    protected void calculateCurrent() {}
    protected void setPoints() {
	dx = x2-x; dy = y2-y;
	dn = Math.sqrt(dx*dx+dy*dy);
	dpx1 = dy/dn;
	dpy1 = -dx/dn;
	dsign = (dy == 0) ? sign(dx) : sign(dy);
	point1 = new Point(x , y );
	point2 = new Point(x2, y2);
    }
    int dotProduct(Point p1, Point p2){
    return(p1.x*p2.x+p1.y*p2.y);}
    
    Point pointDiff(Point p1, Point p2){
    	Point result = new Point();
    	result.x=p1.x-p2.x;result.y=p1.y-p2.y;
    	return(result);
    }
    int distance2diag(int x, int y){
    //	int num;
    //num=Math.abs((point2.y-point1.y)*x - (point2.x-point1.x)*y + point2.x*point1.y-point1.x*point2.y);
    //return((int)(((double)num)/distance(point1,point2)));
    	Point testP = new Point(x,y);
    	Point n = pointDiff(point2,point1);
        Point pa = pointDiff(point1,testP);// a - p;
     
        int c = dotProduct( n, pa );
     
        // Closest point is a
        if ( c > 0 )
            return dotProduct( pa, pa );
     
        Point bp = pointDiff(testP,point2);
     
        // Closest point is b
        if ( dotProduct( n, bp ) > 0 )
            return dotProduct( bp, bp );
     
        // Closest point is between a and b
       
        //int num;
        int num=Math.abs((point2.y-point1.y)*x - (point2.x-point1.x)*y + point2.x*point1.y-point1.x*point2.y);
        int result = (int)(((double)num)/distance(point1,point2));
        return (result*result);
        //return Dot( e, e );
    
    
    }
    protected void calcLeads(int len) {
	if (dn < len || len == 0) {
	    lead1 = point1;
	    lead2 = point2;
	    return;
	}
	lead1 = interpPoint(point1, point2, (dn-len)/(2*dn));
	lead2 = interpPoint(point1, point2, (dn+len)/(2*dn));
    }
    protected Point interpPoint(Point a, Point b, double f) {
	Point p = new Point();
	interpPoint(a, b, p, f);
	return p;
    }
    protected void interpPoint(Point a, Point b, Point c, double f) {
	//int xpd = b.x-a.x;
	//int ypd = b.y-a.y;
	/*double q = (a.x*(1-f)+b.x*f+.48);
	  System.out.println(q + " " + (int) q);*/
	c.x = (int) Math.round(a.x*(1-f)+b.x*f);
	c.y = (int) Math.round(a.y*(1-f)+b.y*f);
    }
    protected void interpPoint(Point a, Point b, Point c, double f, double g) {
	//int xpd = b.x-a.x;
	//int ypd = b.y-a.y;
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
    }
    protected void interpPointFix(Point a, Point b, Point c, double f, double g) {
    	//int xpd = b.x-a.x;
    	//int ypd = b.y-a.y;
    	int gx = b.y-a.y;
    	int gy = a.x-b.x;
    	//g /= Math.sqrt(gx*gx+gy*gy);
    	c.x = (int) Math.round(a.x*(1-f)+b.x*f+g*gx);
    	c.y = (int) Math.round(a.y*(1-f)+b.y*f+g*gy);
        }
    protected Point interpPoint(Point a, Point b, double f, double g) {
	Point p = new Point();
	interpPoint(a, b, p, f, g);
	return p;
    }
    protected void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
    	// computes points c and d based on points a anb and values f, g
//	int xpd = b.x-a.x;
//	int ypd = b.y-a.y;
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
	d.x = (int) Math.floor(a.x*(1-f)+b.x*f-g*gx+.48);
	d.y = (int) Math.floor(a.y*(1-f)+b.y*f-g*gy+.48);
    }
    protected void draw2Leads(Graphics g) {
	// draw first lead
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);

	// draw second lead
	setVoltageColor(g, volts[1]);
	drawThickLine(g, lead2, point2);
    }
    protected Point [] newPointArray(int n) {
	Point a[] = new Point[n];
	while (n > 0)
	    a[--n] = new Point();
	return a;
    }
	
    protected void drawDots(Graphics g, Point pa, Point pb, double pos) {
    	int ds;
    	if (sim.stoppedCheck.getState() || pos == 0 || !sim.dotsCheckItem.getState())
	    return;
	int dx = pb.x-pa.x;
	int dy = pb.y-pa.y;
	double dn = Math.sqrt(dx*dx+dy*dy);
	if (!sim.printableCheckItem.getState())
	{
	g.setColor(Color.yellow);
	 ds = 16;
	}else
	{	g.setColor(new Color(0, 0, 0));
	 ds = 24;
	}
	
	pos %= ds;
	if (pos < 0)
	    pos += ds;
	double di = 0;
	for (di = pos; di < dn; di += ds) {
	    int x0 = (int) (pa.x+di*dx/dn);
	    int y0 = (int) (pa.y+di*dy/dn);
	    if (!sim.printableCheckItem.getState())
	    g.fillRect(x0-1, y0-1, 4, 4);
	    else
	    	g.fillRect(x0, y0, 4, 4);
	}
    }

    protected Polygon calcArrow(Point a, Point b, double al, double aw) {
	Polygon poly = new Polygon();
	Point p1 = new Point();
	Point p2 = new Point();
	int adx = b.x-a.x;
	int ady = b.y-a.y;
	double l = Math.sqrt(adx*adx+ady*ady);
	poly.addPoint(b.x, b.y);
	interpPoint2(a, b, p1, p2, 1-al/l, aw);
	poly.addPoint(p1.x, p1.y);
	poly.addPoint(p2.x, p2.y);
	return poly;
    }
    protected Polygon createPolygon(Point a, Point b, Point c) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	return p;
    }
    protected Polygon createPolygon(Point a, Point b, Point c, Point d) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	p.addPoint(d.x, d.y);
	return p;
    }
    protected Polygon createPolygon(Point a[]) {
	Polygon p = new Polygon();
	int i;
	for (i = 0; i != a.length; i++)
	    p.addPoint(a[i].x, a[i].y);
	return p;
    }
    protected void drag(int xx, int yy) {
	xx = sim.uitools.snapGrid(xx,true);
	yy = sim.uitools.snapGrid(yy,false);
	 if (noDiagonal) {
	    if (Math.abs(x-xx) < Math.abs(y-yy)) {
		xx = x;
	    } else {
		yy = y;
	    }
	}
	x2 = xx; y2 = yy;
	setPoints();
    }
    public void move(int dx, int dy) {
	x += dx; y += dy; x2 += dx; y2 += dy;
	boundingBox.move(dx, dy);
	setPoints();
    }

    // determine if moving this element by (dx,dy) will put it on top of another element
    public boolean allowMove(int dx, int dy) {
	int nx = x+dx;
	int ny = y+dy;
	int nx2 = x2+dx;
	int ny2 = y2+dy;
	int i;
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = sim.getElm(i);
	    if (ce.x == nx && ce.y == ny && ce.x2 == nx2 && ce.y2 == ny2)
		return false;
	    if (ce.x == nx2 && ce.y == ny2 && ce.x2 == nx && ce.y2 == ny)
		return false;
	}
	return true;
    }
    
    public void movePoint(int n, int dx, int dy) {
	if (n == 0) {
	    x += dx; y += dy;
	} else {
	    x2 += dx; y2 += dy;
	}
	setPoints();
    }
    protected void drawPosts(Graphics g) {
	int i;
	for (i = 0; i != getPostCount(); i++) {
	    Point p = getPost(i);
	    drawPost(g, p.x, p.y, nodes[i]);
	}
    }
    protected void stamp() {}
    protected int getVoltageSourceCount() { return 0; }
    protected int getInternalNodeCount() { return 0; }
    void setNode(int p, int n) { nodes[p] = n; }
    protected void setVoltageSource(int n, int v) { voltSource = v; }
    int getVoltageSource() { return voltSource; }
    public double getVoltageDiff() {
	return volts[0] - volts[1];
    }
    protected boolean nonLinear() { return false; }
     protected int getPostCount() { return 2; }
    int getNode(int n) { return nodes[n]; }
    protected Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : null;
    }
    protected void drawPost(Graphics g, int x0, int y0, int n) {
	if (sim.dragElm == null && !needsHighlight() &&
	    sim.algorithm.getCircuitNode(n).links.size() == 2)
	    return;
	if (sim.mouseMode == CirSim.MODE_DRAG_ROW ||
	    sim.mouseMode == CirSim.MODE_DRAG_COLUMN)
	    return;
	drawPost(g, x0, y0);
	
    }
    void drawPost(Graphics g, int x0, int y0) {
	g.setColor(whiteColor);
	g.fillOval(x0-3, y0-3, 7, 7);
	if (sim.exportEPS)
		sim.g2.fillOval((float)((float)x0-2.5), (float)((float)y0-2.5), (float)5.0, (float)5.0);
	    }
    protected void setBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	boundingBox.setBounds(x1, y1, x2-x1+1, y2-y1+1);
    }
    protected void setBbox(Point p1, Point p2, double w) {
	setBbox(p1.x, p1.y, p2.x, p2.y);
	//int gx = p2.y-p1.y;
	//int gy = p1.x-p2.x;
	int dpx = (int) (dpx1*w);
	int dpy = (int) (dpy1*w);
	adjustBbox(p1.x+dpx, p1.y+dpy, p1.x-dpx, p1.y-dpy);
    }
    protected void adjustBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	x1 = min(boundingBox.x, x1);
	y1 = min(boundingBox.y, y1);
	x2 = max(boundingBox.x+boundingBox.width-1,  x2);
	y2 = max(boundingBox.y+boundingBox.height-1, y2);
	boundingBox.setBounds(x1, y1, x2-x1, y2-y1);
    }
    protected void adjustBbox(Point p1, Point p2) {
	adjustBbox(p1.x, p1.y, p2.x, p2.y);
    }
    protected boolean isCenteredText() { return false; }
	
    protected void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
    	FontMetrics fm;
    	int w;
    	if  (sim.exportEPS && !sim.g2.equals(g)){
    		
    		/*sim.g2.setFont(new Font("Arial", 0, 10));
    		FontMetrics fm = g.getFontMetrics();
    		System.out.println("g " + fm.getAscent());
    		drawCenteredText(sim.g2,  s, x,  y,  cx); 
    		fm = sim.g2.getFontMetrics();
    		System.out.println("g2 " + fm.getAscent());*/
    		sim.g2.setFont(new Font("Arial", 0, 10));
    		fm = sim.g2.getFontMetrics();
    		 w = fm.stringWidth(s);
    		if (cx)
    		   x -= w/2;
    //    	sim.g2.setFont(new Font("SansSerif", 0, 5));
    		sim.g2.drawString(s, x-1, y+(fm.getAscent())/2-1);  
    		}
    	 fm = g.getFontMetrics();
	w = fm.stringWidth(s);
	if (cx)
	    x -= w/2;
	g.drawString(s, x, y+fm.getAscent()/2);
	 
	adjustBbox(x, y-fm.getAscent()/2,
		   x+w, y+fm.getAscent()/2+fm.getDescent());
	
    }
    
    protected void drawValues(Graphics g, String s, double hs) {
    	 
	if (s == null)
	    return;
	g.setFont(unitsFont);
	FontMetrics fm = g.getFontMetrics();
	int w = fm.stringWidth(s);
	g.setColor(whiteColor);
	int ya = fm.getAscent()/2;
	int xc, yc;
	if (this instanceof RailElm || this instanceof SweepElm) {
	    xc = x2;
	    yc = y2;
	} else {
	    xc = (x2+x)/2;
	    yc = (y2+y)/2;
	}
	int dpx = (int) (dpx1*hs);
	int dpy = (int) (dpy1*hs);
	if (dpx == 0) {
	    g.drawString(s, xc-w/2, yc-abs(dpy)-6);//-2);
	    if (sim.exportEPS&& !sim.g2.equals(g)){
 	      	//drawValues(sim.g2, s, hs);
 	    	sim.g2.setFont(new Font("Arial", 0, 10));
 	    	//System.out.println("g " + fm.stringWidth(s));
 	    	fm = sim.g2.getFontMetrics();
 	    	//System.out.println("g2 " + fm.stringWidth(s));
 	    	sim.g2.drawString(s, xc-fm.stringWidth(s)/2, yc-abs(dpy)-6);
 	    	//System.out.println("x coord: " + (xc-w/2));
 	    	//System.out.println("y coord: " + (yc-abs(dpy)-6));
 	    	//System.out.println("width: " + (w));
 	      	}
	} else {
	    int xx = xc+abs(dpx)+6;//+2;
	    if (this instanceof VoltageElm || (x < x2 && y > y2))
		xx = xc-(w+abs(dpx)+2);
	    g.drawString(s, xx, yc+dpy+ya);
	    if (sim.exportEPS&& !sim.g2.equals(g)){
	    	sim.g2.setFont(new Font("Arial", 0, 10));
 	      	//drawValues(sim.g2, s, hs);
 	    	//sim.g2.setFont(unitsFont);
 	    	sim.g2.drawString(s, xx, yc+dpy+ya);;
 	    	//System.out.println("WIDTH: " + (w));
 	    	//System.out.println("x coord: " + (xx));
 	      	}
	}
    }
    protected void drawCoil(Graphics g, int hs, Point p1, Point p2,
		  double v1, double v2) {
	//double len = distance(p1, p2);
	int segments = 30; // 10*(int) (len/10);
	int i;
	double segf = 1./segments;
	    
	ps1.setLocation(p1);
	for (i = 0; i != segments; i++) {
	    double cx = (((i+1)*6.*segf) % 2)-1;
	    double hsx = Math.sqrt(1-cx*cx);
	    if (hsx < 0)
		hsx = -hsx;
	    interpPoint(p1, p2, ps2, i*segf, hsx*hs);
	    double v = v1+(v2-v1)*i/segments;
	    setVoltageColor(g, v);
	    drawThickLine(g, ps1, ps2);
	    ps1.setLocation(ps2);
	}
    }
    protected static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
	g.drawLine(x, y, x2, y2);
	g.drawLine(x+1, y, x2+1, y2);
	g.drawLine(x, y+1, x2, y2+1);
	g.drawLine(x+1, y+1, x2+1, y2+1);
	if (sim.exportEPS)
	sim.g2.drawLine(x, y, x2, y2);
    }

    protected static void drawThickLine(Graphics g, Point pa, Point pb) {
	g.drawLine(pa.x, pa.y, pb.x, pb.y);
	g.drawLine(pa.x+1, pa.y, pb.x+1, pb.y);
	g.drawLine(pa.x, pa.y+1, pb.x, pb.y+1);
	g.drawLine(pa.x+1, pa.y+1, pb.x+1, pb.y+1);
	if (sim.printableCheckItem.getState())
	{g.drawLine(pa.x-1, pa.y, pb.x-1, pb.y);
	g.drawLine(pa.x, pa.y-1, pb.x, pb.y-1);
	g.drawLine(pa.x-1, pa.y-1, pb.x-1, pb.y-1);}
	
	if (sim.exportEPS) {
	//	System.out.println(sim.g2);
	sim.g2.drawLine(pa.x, pa.y, pb.x, pb.y);}
//	sim.gg.drawLine(pa.x, pa.y, pb.x, pb.y);
    }

    protected static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
	int i;
	for (i = 0; i != c-1; i++)
	    drawThickLine(g, xs[i], ys[i], xs[i+1], ys[i+1]);
	drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
    }
    
    protected static void drawThickPolygon(Graphics g, Polygon p) {
	drawThickPolygon(g, p.xpoints, p.ypoints, p.npoints);
    }
    
    protected static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
	int a;
	int step = 20;
	double m = pi/180;
	double r = ri*.98;
	if (sim.exportEPS){
		sim.g2.drawOval(cx-ri,cy-ri,2*ri,2*ri);
		return;
		}
	for (a = 0; a != 360; a += step) {
	    double ax = Math.cos(a*m)*r + cx;
	    double ay = Math.sin(a*m)*r + cy;
	    double bx = Math.cos((a+20)*m)*r + cx;
	    double by = Math.sin((a+20)*m)*r + cy;
	    drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
	}
    }
    static void drawPrecisionCircle(Graphics g, int cx, int cy, int ri) {
    	int a;
    	double m = pi/180;
    	double r = ri*.98;
    	if (sim.exportEPS){
    		sim.g2.drawOval(cx-ri,cy-ri,2*ri,2*ri);
    		return;
    		}
    	for (a = 0; a != 360; a += 10) {
    	    double ax = Math.cos(a*m)*r + cx;
    	    double ay = Math.sin(a*m)*r + cy;
    	    double bx = Math.cos((a+10)*m)*r + cx;
    	    double by = Math.sin((a+10)*m)*r + cy;
    	    drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
    	}
        }
    protected static String getVoltageDText(double v) {
	return getUnitText(Math.abs(v), "V");
    }
    public static String getVoltageText(double v) {
	return getUnitText(v, "V");
    }
    public static String getUnitText(double v, String u) {
	double va = Math.abs(v);
	if (va < 1e-14)
	    return "0 " + u;
	if (va < 1e-9)
	    return showFormat.format(v*1e12) + " p" + u;
	if (va < 1e-6)
	    return showFormat.format(v*1e9) + " n" + u;
	if (va < 1e-3)
	    return showFormat.format(v*1e6) + " " + CirSim.muString + u;
	if (va < 1)
	    return showFormat.format(v*1e3) + " m" + u;
	if (va < 1e3)
	    return showFormat.format(v) + " " + u;
	if (va < 1e6)
	    return showFormat.format(v*1e-3) + " k" + u;
	if (va < 1e9)
	    return showFormat.format(v*1e-6) + " M" + u;
	return showFormat.format(v*1e-9) + " G" + u;
    }
    protected static String getShortUnitText(double v, String u) {
	double va = Math.abs(v);
	if (va < 1e-13)
	    return null;
	if (va < 1e-9)
	    return shortFormat.format(v*1e12) + "p" + u;
	if (va < 1e-6)
	    return shortFormat.format(v*1e9) + "n" + u;
	if (va < 1e-3)
	    return shortFormat.format(v*1e6) + CirSim.muString + u;
	if (va < 1)
	    return shortFormat.format(v*1e3) + "m" + u;
	if (va < 1e3)
	    return shortFormat.format(v) + u;
	if (va < 1e6)
	    return shortFormat.format(v*1e-3) + "k" + u;
	if (va < 1e9)
	    return shortFormat.format(v*1e-6) + "M" + u;
	return shortFormat.format(v*1e-9) + "G" + u;
    }
    public static String getCurrentText(double i) {
	return getUnitText(i, "A");
    }
    protected static String getCurrentDText(double i) {
	return getUnitText(Math.abs(i), "A");
    }

    protected void updateDotCount() {
	curcount = updateDotCount(current, curcount);
    }
    protected double updateDotCount(double cur, double cc) {
	if (sim.stoppedCheck.getState())
	    return cc;
	double cadd = cur*currentMult;
	/*if (cur != 0 && cadd <= .05 && cadd >= -.05)
	  cadd = (cadd < 0) ? -.05 : .05;*/
	cadd %= 8;
	/*if (cadd > 8)
	  cadd = 8;
	  if (cadd < -8)
	  cadd = -8;*/
	return cc + cadd;
    }
    protected void doDots(Graphics g) {
	updateDotCount();
	if (sim.dragElm != this)
	    drawDots(g, point1, point2, curcount);
    }
    void doAdjust() {}
    void setupAdjust() {}
    protected void getInfo(String arr[]) {
    }
    protected int getBasicInfo(String arr[]) {
	arr[1] = "I = " + getCurrentDText(getCurrent());
	arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
	return 3;
    }
    protected void setVoltageColor(Graphics g, double volts) {
	if (needsHighlight()) {
	    g.setColor(selectColor);
	    return;
	}
	if (!sim.voltsCheckItem.getState()) {
	    if (!sim.powerCheckItem.getState()) // && !conductanceCheckItem.getState())
		g.setColor(whiteColor);
	    return;
	}
	int c = (int) ((volts+voltageRange)*(colorScaleCount-1)/
		       (voltageRange*2));
	if (c < 0)
	    c = 0;
	if (c >= colorScaleCount)
	    c = colorScaleCount-1;
	g.setColor(colorScale[c]);
    }
    protected void setPowerColor(Graphics g, boolean yellow) {
	/*if (conductanceCheckItem.getState()) {
	  setConductanceColor(g, current/getVoltageDiff());
	  return;
	  }*/
	if (!sim.powerCheckItem.getState())
	    return;
	setPowerColor(g, getPower());
    }
    protected void setPowerColor(Graphics g, double w0) {
	w0 *= powerMult;
	//System.out.println(w);
	double w = (w0 < 0) ? -w0 : w0;
	if (w > 1)
	    w = 1;
	int rg = 128+(int) (w*127);
	int b  = (int) (128*(1-w));
	/*if (yellow)
	  g.setColor(new Color(rg, rg, b));
	  else */
	if (w0 > 0)
	    g.setColor(new Color(rg, b, b));
	else
	    g.setColor(new Color(b, rg, b));
    }
    void setConductanceColor(Graphics g, double w0) {
	w0 *= powerMult;
	//System.out.println(w);
	double w = (w0 < 0) ? -w0 : w0;
	if (w > 1)
	    w = 1;
	int rg = (int) (w*255);
	g.setColor(new Color(rg, rg, rg));
    }
    protected double getPower() { return getVoltageDiff()*current; }
    public double getScopeValue(int x) {
	return (x == 1) ? getPower() : getVoltageDiff();
    }
    public String getScopeUnits(int x) {
	return (x == 1) ? "W" : "V";
    }
    protected boolean needsArduino(){ return false;};
    public EditInfo getEditInfo(int n) { return null; }
    public void setEditValue(int n, EditInfo ei) {}
    protected boolean getConnection(int n1, int n2) { return true; }
    protected boolean hasGroundConnection(int n1) { return false; }
    protected boolean isWire() { return false; }
    protected boolean canViewInScope() { return getPostCount() <= 2; }
    protected boolean comparePair(int x1, int x2, int y1, int y2) {
	return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
    }
    protected boolean needsHighlight() { return sim.mouseElm == this || selected; }
    public boolean isSelected() { return selected; }
   public void setSelected(boolean x) { 
	   selected = x; 
	   /*if (sim.usePanel)
	   try {
			sim.proxy.feval("pg_select_PG_elements",this);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	   }
    public void selectRect(Rectangle r) {
	selected = r.contains(boundingBox);//r.intersects(boundingBox);
    }
    public static int abs(int x) { return x < 0 ? -x : x; }
    public static int sign(int x) { return (x < 0) ? -1 : (x == 0) ? 0 : 1; }
    protected static int min(int a, int b) { return (a < b) ? a : b; }
    protected static int max(int a, int b) { return (a > b) ? a : b; }
    protected static double distance(Point p1, Point p2) {
	double x = p1.x-p2.x;
	double y = p1.y-p2.y;
	return Math.sqrt(x*x+y*y);
    }
    public Rectangle p_getBoundingBox() { return boundingBox; }
    Rectangle getBoundingBox() { return boundingBox; }
    protected boolean needsShortcut() { return false; }
}
