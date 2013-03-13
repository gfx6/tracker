package com.gfx6.tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	LocationManager lmanager;
	LocationListener locListener;
	float velMax=0;
	TextView txtVel,txtvelmax,txtloc,txtpres,txthora;
	LinkedList<Float> vels= new LinkedList<Float>();
	LinkedList<Posicion> posiciones= new LinkedList<Posicion>();
	ProgressDialog dp;
	Button btnStart,btnGen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtVel=(TextView)findViewById(R.id.txtvelprom);
		txtloc=(TextView)findViewById(R.id.txtLocalizacion);
		txthora=(TextView)findViewById(R.id.txtGPSHora);
		txtpres=(TextView)findViewById(R.id.txtPres);
		txtvelmax=(TextView)findViewById(R.id.txtvelmax);
		btnStart=(Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(listener);
		btnGen=(Button)findViewById(R.id.btnGuarda);
		btnGen.setOnClickListener(listenerGen);
		dp= new ProgressDialog(this);
	}
	private OnClickListener listenerGen=new OnClickListener()
	{

		@Override
		public void onClick(View v) {
		String contenido=GeneraKML(posiciones);
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
		int hora = c.get(Calendar.HOUR);
		int dia = c.get(Calendar.DAY_OF_YEAR);
		
			writeFile("prueba_"+dia+hora+seconds+".kml",contenido);
		}};
	
	
	
	private OnClickListener listener= new OnClickListener()
	{

		@Override
		public void onClick(View arg0) {
			
			dp.setTitle("Coordenadas GPS");
			dp.setMessage("Esperando informacion GPS");
			dp.show();
			ConfigGPS();
			
		}};
	private String GeneraKML(LinkedList<Posicion> pos)
	{
		char newLine='\n';
		String cabecera="<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://www.opengis.net/kml/2.2\"><Document>    <name>Paths</name>    <description>Examples of paths. Note that the tessellate tag is by default      set to 0. If you want to create tessellated lines, they must be authored      (or edited) directly in KML.</description>    <Style id=\"yellowLineGreenPoly\">      <LineStyle>        <color>7f00ffff</color>        <width>4</width>      </LineStyle>      <PolyStyle>        <color>7f00ff00</color>      </PolyStyle>    </Style>    <Placemark>      <name>Absolute Extruded</name>      <description>Transparent green wall with yellow outlines</description>      <styleUrl>#yellowLineGreenPoly</styleUrl>      <LineString>        <extrude>1</extrude>        <tessellate>1</tessellate>        <altitudeMode>absolute</altitudeMode><coordinates>";
		StringBuilder sb=new StringBuilder();
		sb.append(cabecera);
		for(Posicion posAux : pos)
		{
			
			sb.append(Double.toString(posAux.getLongitud()) + "," + Double.toString(posAux.getLatitud()) + "," + Double.toString(posAux.getVelocidad()+10) );
			sb.append(newLine);
		}
		sb.append("</coordinates>      </LineString></Placemark>  </Document></kml>");
		return sb.toString();
	}
	private void ConfigGPS()
	{
		lmanager= (LocationManager)this.getSystemService(LOCATION_SERVICE);
		locListener=new LocationListener()
		{

			@Override
			public void onLocationChanged(Location location) {
				Posicion posAux=new Posicion();
				posAux.setLatitud(location.getLatitude());
				posAux.setLongitud(location.getLongitude());
				posAux.setVelocidad(location.getAltitude()+location.getSpeed());
				posiciones.add(posAux);
				txtloc.append("LA:" +Double.toString(location.getLatitude())+ ", LO:"+ Double.toString(location.getLongitude()) + "\r\n");
				if (location.getSpeed()>1.0)
				{
					vels.add(location.getSpeed());
					double prom=0.0;
					for(float f :vels)
					{
						prom+=f;
					}
					prom=prom/vels.size();
					txtVel.setText(Double.toString(prom));
					if (location.getSpeed()>velMax)
					{
						velMax=location.getSpeed();
						txtvelmax.setText(Float.toString(velMax));
					}
				}
				//txtVel.setText(Float.toString(location.getSpeed()));						
				txthora.setText( DateFormat.format("MM/dd/yy h:mmaa",location.getTime()));
				txtpres.setText(Float.toString(location.getAccuracy()));
				dp.dismiss();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}};
		lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2,locListener);
	}
	
	public static boolean isExternalStorageReadOnly() {  
		 String extStorageState = Environment.getExternalStorageState();  
		     if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {  
		         return true;  
		     }  
		     return false;  
		}  
		   
		public static boolean isExternalStorageAvailable() {  
		     String extStorageState = Environment.getExternalStorageState();  
		     if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {  
		         return true;  
		     }  
		     return false;  
		} 
		public void writeFile(String filename, String textfile){
			try {
			  if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) { 
			   File file = new File(Environment.getExternalStorageDirectory(), filename );
			   OutputStreamWriter outw = new OutputStreamWriter(new FileOutputStream(file));
			   outw.write(textfile);
			   outw.close();
			}
			} catch (Exception e) {}  
			}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
