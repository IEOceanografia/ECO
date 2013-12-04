import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class ECO extends PApplet {

// Declaring a variable of type PImage
PImage img;
PFont f, f2, f3;
toolbar tools;
ADRadio radioButton, radioButton2;

int bkgndColor = 0;
float x;
float a1 = 10;
int index_frate = 0;
int index_frate2 = 0;
int count;
boolean change;
int lengthfile = 20618;
int c1, c2;
boolean uptake=true;
float acumula=0;
int contador=0;
int radio1_old, radio2_old;
float a1_old=0;

int colorfondo = color(68,169,188);
int colorrelease = color(175,37,81);

String[] options = {"NCEP-1","NCEP-2","ERA-Interim","QuikSCAT","HIRLAM-AEMet"};
String[] options2 = {"Nightingale et al. 2002","Ho et al. 2006","Sweeney et al. 2007","Wannikhof et al. 1992","Liss & Merlivat 1986"};
String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

String[] lines;	
  float[] time = new float[206918];
  float[] lon = new float[20618];
  float[] lat = new float[20618];
  float[] ncep = new float[20618];
  float[] ncep2 = new float[20618];
  float[] quikscat = new float[20618];
  float[] era = new float[20618];
  float[] hirlam = new float[20618];
  float[] sCO2 = new float[20618];
  float[] grad = new float[20618];
  float[] k600 = new float[20618];
  float[] k660 = new float[20618];
  float[] wind = new float[20618];
  int[] route = new int[20618];
  int[] time_year = new int[20618];
  int[] time_month = new int[20618];
  int[] time_day = new int[20618]; 
  
  float k;
  float[] FCO2 = new float[501];
  float[] FCO2_old = new float[501];
  float[] latroute = new float[501];
  
public void setup() {
  size(600,600);
  
  // Make a new instance of a PImage by loading an image file
  img = loadImage("Mapa_processing_recortado2.png");
  
  //Load fonts
  f = loadFont("ArialMT-16.vlw"); 
  f2 = loadFont("Arial-Black-16.vlw");
  f3 = loadFont("Borealis-16.vlw");
  
  //Load the data file
  lines = loadStrings("ECO_allwinds_onroute_withCO2_20120627.txt"); 

  //Fill variable with data
  for (int index = 0; index < lines.length; index = index + 1) {
     String[] data = split(lines[index], '\t');
     if (data.length == 19) {
       time[index] = PApplet.parseFloat(data[0]);
       lon[index] = PApplet.parseFloat(data[1]);
       lat[index] = PApplet.parseFloat(data[2]);
       ncep[index] = PApplet.parseFloat(data[3]);
       ncep2[index] = PApplet.parseFloat(data[4]);
       hirlam[index] = PApplet.parseFloat(data[5]);
       quikscat[index] = PApplet.parseFloat(data[6]);
       era[index] = PApplet.parseFloat(data[7]);
       grad[index] = PApplet.parseFloat(data[11]);
       sCO2[index] = PApplet.parseFloat(data[12]);
       k600[index] = PApplet.parseFloat(data[13]);
       k660[index] = PApplet.parseFloat(data[14]);
       route[index] = PApplet.parseInt(data[15]);
       time_day[index] = PApplet.parseInt(data[16]);
       time_month[index] = PApplet.parseInt(data[17]);
       time_year[index] = PApplet.parseInt(data[18]);
     }
   }  

  //Set up GUI controls
  float[] pos = {
    5, 140, width-20, 40
  };
  int gray1 = 0xff2E4F55;
  int gray2 = darken(gray1);
  int gray3 = darken(gray2);
  tools = new toolbar(pos, 10);
  tools.addSlider("Time", "left", gray2, gray3, 2452602, 2452914);
  ((slider) tools.lastAdded()).setVal(2452603);
 
  //Add radio button to winds
  radioButton = new ADRadio(width-280, 70, options, "radioButton");
  radioButton.setDebugOn();
  radioButton.setBoxFillColor(0xffF7ECD4); 
  radioButton.setValue(1);
  radio1_old=radioButton.getValue();
  
  //Add radio button to algorithms
  radioButton2 = new ADRadio(width-150, 70, options2, "radioButton");
  radioButton2.setDebugOn();
  radioButton2.setBoxFillColor(0xffF7ECD4); 
  radioButton2.setValue(3);
  radio2_old=radioButton2.getValue();
  
  //Figure with the sea-air interface;
  c2=darken(darken(darken(colorfondo)));
  c1 = darken(colorfondo); 
  
  //Points will be plotted regularly along the route to allow comparisons
  for(int index2 = 0; index2<=500; index2 = index2 + 1) { 
    latroute[index2]=42.25f+0.01f*index2;
    FCO2[index2]=0;
  }
}

public void draw() {
  smooth();
  background(colorfondo);
  fill(0);
  stroke(0); rect(0,200,width,height);
  textFont(f,16);
  tools.update();
  radioButton.update();
  radioButton2.update();
  a1 = ((slider) tools.find("Time")).getVal();
 
  stroke(darken(darken(darken(colorfondo))));
  text("CO2",235,150); 
  textFont(f,7);
  text("Air",190,160);
  text("Sea",190,173);
  
  fill(c1); stroke(c1);
  textFont(f,10);
  text("Release",205,185);
  triangle(220-3,160,220+4+3,160,220+4/2,160-5);
  rect(220, 160, 4, 10);
  for (int i = 200; i < 245; i++) {     
     point(i,165+3*sin( (2*PI/30)*i ) );
  }

  fill(c2); stroke(c2);
  text("Uptake",255,185);
  triangle(270-3,160+10,270+4+3,160+10,270+4/2,160+10+5);
  rect(270, 160, 4, 10);
  for (int i = 250; i < 295; i++) {     
     point(i,165+3*sin( (2*PI/30)*i ) );
  }
 
  // Draw the image to the screen at coordinate (0,0)
  image(img,60,300,1480/3,908/3);
  textFont(f,16);                    // Specify font to be used
  fill(200,200,200);                // Specify font color 
  text("Vigo",125,550);            // Display Text
  text("St. Nazaire",480,350);    // Display Text
  
  //Las latitudes van aprox. desde 42.25 hasta 47.25\u00baN
  float vigo_x = 103; 
  float vigo_y = 548;
  float codo1_x = 79;
  float codo1_y = 519;
  float codo2_x = 105;
  float codo2_y = 490;
  float nazaire_x = 456;
  float nazaire_y = 363;
  
  float width_route_1 = abs(vigo_x - codo1_x); //Tamanho en pixeles de la longitud de la ruta
  float width_route_2 = abs(codo1_x - codo2_x);
  float width_route_3 = abs(codo2_x - nazaire_x);
  float height_route_1 = abs(vigo_y - codo1_y); //Tamanho en pixeles de la longitud de la ruta
  float height_route_2 = abs(codo1_y - codo2_y);
  float height_route_3 = abs(codo2_y - nazaire_y);
 
 
 if (radioButton.getValue()!=radio1_old || radioButton2.getValue()!=radio2_old || a1!=a1_old) {
   radio1_old=radioButton.getValue();
   radio2_old=radioButton2.getValue();
   a1_old=a1;
   index_frate=0;
   index_frate2=0;
   change=true;
   acumula=0;
   contador=0;
 }
 
 //Which is the selected wind?
 if(radioButton.getValue() == 0) {
   wind=ncep;
 } else if (radioButton.getValue() == 1) {
   wind=ncep2;
 } else if (radioButton.getValue() == 2) {
   wind=era;  
 } else if (radioButton.getValue() == 3) {
   wind=quikscat;    
 } else if (radioButton.getValue() == 4) {
   wind=hirlam;      
 }            
 
 //Explore data looking for the exact selected time
 for (int index = 0; index < lines.length; index = index + 1) {
   if(time[index]-a1 >= 0) {
     count = index;
     break;
   }
 }

if(frameCount==0 || change) {

  for(int index2 = 0; index2<=500; index2 = index2 + 1) {   
    FCO2_old[index2]=FCO2[index2];   
    FCO2[index2]=0; 
  }     
  
  acumula=0;
  contador=0;
  for (int index = 0; index < lines.length; index = index + 1) { 
    if(route[index]==route[count] && lat[index]>=42.25f && lat[index]<=47.25f){
     for(int index2 = 0; index2<=500; index2 = index2 + 1) {      
       if(abs(lat[index]-latroute[index2])<=0.01f && wind[index]!=-9999 && k600[index]!=-9999 && k660[index]!=-9999 && sCO2[index]!=-9999 && grad[index]!=-9999 && grad[index]<250) {       
           //And the algorithm?
           if(radioButton2.getValue() == 0) {
              k=(0.222f*pow(wind[index],2)+0.333f*wind[index])*sqrt(k600[index]);
           } else if (radioButton2.getValue() == 1) {
              k=(0.266f*pow(wind[index],2))*sqrt(k600[index]);
           } else if (radioButton2.getValue() == 2) {
              k=(0.27f*pow(wind[index],2))*sqrt(k660[index]);
           } else if (radioButton2.getValue() == 3) {
              k=(0.31f*pow(wind[index],2))*sqrt(k660[index]);   
           } else if (radioButton2.getValue() == 4) {
              if(wind[index]<=3.6f) {
                k=0.17f*wind[index]*pow(k600[index],2/3);
              } else if (wind[index]>3.6f & wind[index]<=13.0f) {
                k=(2.85f*wind[index]-9.65f)*sqrt(k600[index]);
              } else {
                k=(5.9f*wind[index]-49.3f)*sqrt(k600[index]);   
              }  
            }   
           FCO2[index2] = 0.08766f * k * sCO2[index] * grad[index];
           //print(FCO2[index2] + " "); 
         } 
      }
   }
 }
  change=false;
} //end if frame=0  



  strokeWeight(0.5f);
  for(int index2 = 0; index2<=500; index2 = index2 + 1) {      
     if(uptake && FCO2[index2]>0 && FCO2_old[index2]>=0) {    
           if(latroute[index2]>=42.25f && latroute[index2]<=42.8f) {
             float desfase_x = width_route_1*(latroute[index2]-42.25f)/(42.8f-42.25f);
             float desfase_y = height_route_1*(latroute[index2]-42.25f)/(42.8f-42.25f);
             stroke(colorfondo);  
             line(vigo_x-desfase_x,vigo_y-desfase_y,vigo_x-desfase_x,vigo_y-desfase_y-(FCO2_old[index2]+(FCO2[index2]-FCO2_old[index2])*index_frate/60)*10);
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-FCO2_old[index2]*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) {  
               stroke(216,207,22,256-(index_frate2-60)*256/60);    
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-FCO2_old[index2]*10,1,1);          
             }               
           } else if(latroute[index2]>42.8f && latroute[index2]<=43.2f) {
             float desfase_x = width_route_2*(latroute[index2]-42.8f)/(43.2f-42.8f);
             float desfase_y = height_route_2*(latroute[index2]-42.8f)/(43.2f-42.8f);  
             stroke(colorfondo);        
             line(codo1_x+desfase_x,codo1_y-desfase_y,codo1_x+desfase_x,codo1_y-desfase_y-(FCO2_old[index2]+(FCO2[index2]-FCO2_old[index2])*index_frate/60)*10);        
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-FCO2_old[index2]*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-FCO2_old[index2]*10,1,1);
             }            
           } else if(latroute[index2]>43.2f && latroute[index2]<47.25f) {  
             float desfase_x = width_route_3*(latroute[index2]-43.2f)/(47.25f-43.2f);
             float desfase_y = height_route_3*(latroute[index2]-43.2f)/(47.25f-43.2f);   
             stroke(colorfondo);             
             line(codo2_x+desfase_x,codo2_y-desfase_y,codo2_x+desfase_x,codo2_y-desfase_y-(FCO2_old[index2]+(FCO2[index2]-FCO2_old[index2])*index_frate/60)*10);       
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo2_x+desfase_x,codo2_y-desfase_y-FCO2_old[index2]*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo2_x+desfase_x,codo2_y-desfase_y-FCO2_old[index2]*10,1,1);
             } 
             
           }
             //Compute the mean over the route
             if(FCO2[index2]>0) {
              acumula+=FCO2[index2];
              contador++;
             }           
          } else if (!uptake && FCO2[index2]<0 && FCO2[index2]!=-9999) {            
             if(latroute[index2]>=42.25f && latroute[index2]<=42.8f) {
             float desfase_x = width_route_1*(latroute[index2]-42.25f)/(42.8f-42.25f);
             float desfase_y = height_route_1*(latroute[index2]-42.25f)/(42.8f-42.25f);
             stroke(colorrelease);   
             line(vigo_x-desfase_x,vigo_y-desfase_y,vigo_x-desfase_x,vigo_y-desfase_y-(abs(FCO2_old[index2])+(abs(FCO2[index2])-abs(FCO2_old[index2]))*index_frate/60)*10);
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) {  
               stroke(216,207,22,256-(index_frate2-60)*256/60);    
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-abs(FCO2_old[index2])*10,1,1);          
             }                     
            } else if(latroute[index2]>42.8f && lat[index2]<=43.2f) {
             float desfase_x = width_route_2*(latroute[index2]-42.8f)/(43.2f-42.8f);
             float desfase_y = height_route_2*(latroute[index2]-42.8f)/(43.2f-42.8f);
             stroke(colorrelease);   
             line(codo1_x+desfase_x,codo1_y-desfase_y,codo1_x+desfase_x,codo1_y-desfase_y-(abs(FCO2_old[index2])+(abs(FCO2[index2])-abs(FCO2_old[index2]))*index_frate/60)*10);   
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             }       
           } else if(latroute[index2]>43.2f && latroute[index2]<47.25f) {  
             float desfase_x = width_route_3*(latroute[index2]-43.2f)/(47.25f-43.2f);
             float desfase_y = height_route_3*(latroute[index2]-43.2f)/(47.25f-43.2f);
             stroke(colorrelease);   
             line(codo2_x+desfase_x,codo2_y-desfase_y,codo2_x+desfase_x,codo2_y-desfase_y-(abs(FCO2_old[index2])+(abs(FCO2[index2])-abs(FCO2_old[index2]))*index_frate/60)*10);   
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo2_x+desfase_x,codo2_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo2_x+desfase_x,codo2_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             }  
          }
             //Compute the mean over the route
             if(FCO2[index2]!=-9999 && FCO2[index2]<0) {
              acumula+=abs(FCO2[index2]);
              contador++;
             } 
          }  
  } // end loop trough data
  
 
  //Compute mean values along the route
  if(uptake) {
     stroke(colorfondo);  fill(colorfondo); 
     rect(0,220,120*sqrt(acumula/contador/PI),10);
     textFont(f2,11);
     text("Mean CO2 flux uptake along the route (mmol m-2 day-1)",5,245);
     textFont(f2,12);
     text(acumula/contador/PI,120*sqrt(acumula/contador/PI)+5,220); 
  } else {
     stroke(colorrelease);  fill(colorrelease); 
     rect(0,220,120*sqrt(acumula/contador/PI),10);
     textFont(f2,11);
     text("Mean CO2 flux release along the route (mmol m-2 day-1)",5,245);
     textFont(f2,12);
     text(acumula/contador/PI,120*sqrt(acumula/contador/PI)+5,220); 
  }
  
    annotate();
  
  if(index_frate<60) {
    index_frate++;
  }  
  if(index_frate2<120) {
    index_frate2++;
  } 
  
} // end draw()



public void mousePressed() {
  boolean captured = tools.offerMousePress();  
 
  if(mouseX>220 && mouseX<245 && mouseY>155 && mouseY<177) {
    c1 = darken(darken(darken(colorfondo)));
    c2 = darken(colorfondo);
    uptake=false;
    acumula=0;
    contador=0;
   } 
  if(mouseX>250 && mouseX<295 && mouseY>155 && mouseY<177) {
    c1 = darken(colorfondo);
    c2 = darken(darken(darken(colorfondo)));
    uptake=true;
    acumula=0;
    contador=0;
   }    
   
}

public void annotate() {
      stroke(darken(colorfondo)); fill(darken(colorfondo));
     ellipse(30,30,40,40);
     stroke(240); fill(240);
     triangle(24,20,24,40,44,30);
     stroke(darken(darken(darken(colorfondo)))); fill(darken(darken(darken(colorfondo))));
     textFont(f2,24);
     text("Play", 30, 25);
     textFont(f3,18);
     text("With",3,40);
     textFont(f3,22);
     text("me",35,50);     
     text("Winds",320,50);
     text("Algorithms",470,50);
     line(88,10,295,10);
     line(295,10,295,130);
     line(295,130,10,130);
     line(10,130,10,42);
     textFont(f2,10);
     text("Explore the sea-air CO2 flux in the",100,30);
     text("Bay of Biscay during a complete",100,45);
     text("year cycle.",100,60);
     textFont(f,11);
     text("Select a wind product to interpolate along the route and",20,75);
     text("also one of the most widely used gas transfer velocity",20,90);
     text("parameterizations. Choose between uptake (CO2 is", 20,105);
     text("captured by the ocean) or release (from sea to the air).",20,120);
  
}


// version 1.0

class guiElement {
  String name = "";
  float width, height, x0, y0;
  
  public boolean update() {return false;}
  
  public boolean offerMousePress() {return false;}
  
  public float[] position() {
    float[] r = {x0, y0, width, height};
    return r;
  }  
}



class button extends guiElement {
  int neutralColor, activeColor, highlightColor;
  int backgroundColor = color(255);
  // these make a color palette, that can be used in different ways.
  // use foreColor() and bkgndColor() for drawing.
  boolean active = false;
  boolean pressed = false;
  boolean hidden = false;
  int thetextsize = 9;
  float leading = 0.8f;
  
  public void construct(String name0, float[] pos, int neutral, int actv) {
    neutralColor = neutral;
    activeColor = actv;
    highlightColor = darken(activeColor);
    x0 = pos[0]; y0 = pos[1]; width = pos[2]; height = pos[3];
    name = name0;
  }

  public void drawFace() {
    noStroke();
    fill(foreColor());
    rectMode(CORNER);
    rect(x0,y0,width,height);
  }
  
  public void drawName() {
    fill(bkgndColor());
    textAlign(CENTER);
    textSize(thetextsize);
    textLeading(leading*thetextsize);
    text(name,x0+width/2,y0+height/2+thetextsize/2);
  }
  
  public int foreColor() {
    if (pressed) {
      return(highlightColor);
    } else if (over()) {
      return(activeColor);
    } else {
      return(neutralColor);
    }
  }
  
  public int bkgndColor() {
    return backgroundColor;
  }
  
  public void draw() {
    if (!hidden) {
      drawFace();
      drawName();
    }
  }
  
  public boolean over() {
    return (((mouseX>=x0) && (mouseX<=x0+width)) && ((mouseY>=y0) && (mouseY<=y0+height)));
  }
  
  public boolean offerMousePress() {
    pressed = over() && (!hidden);
    return pressed;
  }
  
  public boolean update() {
    // returns true when the button is released.
    boolean result = false;
    if (pressed) {
      active = over();
      pressed = mousePressed;
    }
    if (active && (!mousePressed)) {
      pressed = false;
      active = false;
      result = true;
    } 
    draw();
    return result;
  }
  
}


class textButton extends button {

  textButton(String name0, float[] pos, int neutral, int actv) {
    construct(name0, pos, neutral, actv);
    thetextsize = round(height/3);
  }   
}


class polyButton extends button {
  
  float[] px,py; // coords of the polygon
  boolean drawSecondPoly = false;
  float[] px2, py2; // optional second polygon, for more complex shapes
  boolean drawCutoutPoly = false;
  float[] cx,cy; // option extra polygon in bkgndColor() instead of foreColor()
  boolean showName = true;
  
  polyButton(String name0, float[] pos, int neutral, int actv, float[] xx, float[] yy) {
    construct(name0, pos, neutral, actv);
    thetextsize = round(height/3);
    definePoly(xx,yy);
  }   

  public void definePoly(float[] xx, float[] yy) {
    px = xx;
    py = yy;
  }
  
  public void defineSecondPoly(float[] xx, float[] yy) {
    drawSecondPoly = true;
    px2 = xx;
    py2 = yy;
  }
  
  public void defineCutoutPoly(float[] xx, float[] yy, int col) {
    drawCutoutPoly = true;
    cx = xx;
    cy = yy;
  }
  
  public void drawFace() {
    fill(foreColor());
    noStroke();
    beginShape(POLYGON);
    for (int i=0; i<px.length; i++) {
      vertex(x0+width*px[i],y0+height*(1-py[i]));
    }
    endShape();
    if (drawSecondPoly) {
      beginShape(POLYGON);
      for (int i=0; i<px2.length; i++) {
        vertex(x0+width*px2[i],y0+height*(1-py2[i]));
      }
    }
    if (drawCutoutPoly) {
      fill(bkgndColor());
      beginShape(POLYGON);
      for (int i=0; i<px2.length; i++) {
        vertex(x0+width*px2[i],y0+height*(1-py2[i]));
      }
    }
  }
  
  public void drawName() {
    if (showName) {
      textSize(thetextsize);
      textAlign(CENTER);
      textLeading(leading*thetextsize);
      fill(foreColor());
      text(name, x0+width/2, y0+height+1.2f*thetextsize);
    }
  }

}




class multistatePolyButton extends guiElement {
  
  polyButton[] states;
  int lastDefined = -1;
  int current = 0;
  boolean hidden = false;
  
  multistatePolyButton(int N, float[] pos) {
    states = new polyButton[N];
    x0 = pos[0]; y0 = pos[1]; width = pos[2]; height = pos[3];
  }
  
  multistatePolyButton(int N, polyButton firstState) {
    states = new polyButton[N];
    addState(firstState);
    x0 = firstState.x0; y0 = firstState.y0; width = firstState.width; height = firstState.height;
  }
  
  multistatePolyButton(String name0, int N, float[] pos) {
    name = name0;
    states = new polyButton[N];
    x0 = pos[0]; y0 = pos[1]; width = pos[2]; height = pos[3];
  }
  
  multistatePolyButton(String name0, int N, polyButton firstState) {
    name = name0;
    states = new polyButton[N];
    addState(firstState);
    x0 = firstState.x0; y0 = firstState.y0; width = firstState.width; height = firstState.height;
  }

  
  
  public int addState(polyButton btn) {
    if (lastDefined < states.length) {
      lastDefined++;
      states[lastDefined] = btn;
      states[lastDefined].hidden = true;
      if (lastDefined==current) {
        syncPosition();
      }
      return lastDefined;
    } else {
      return -1;
    }
  }
  
  public polyButton currentState() {
    return states[current];
  }
  
  public void syncPosition() {
    polyButton s = currentState();
    x0 = s.x0;
    y0 = s.y0;
    width = s.width;
    height = s.height;
  }
  
  public boolean update() {
    boolean result = false;
    if (!hidden) {
      states[current].hidden = false;
      result = states[current].update();
      if (result) {
        states[current].hidden = true;
        current++;
        if (current==states.length) {current=0;}
        syncPosition();
      }
    }
    return result;
  }
  
  public void draw() {
    if (!hidden) {
      states[current].draw();
    }
  }
  
  public boolean offerMousePress() {
    boolean captured = false;
    if (!hidden) {
      captured = states[current].offerMousePress();
    }
    return captured;
  }
  
}



class slider extends guiElement {
  int neutralColor, activeColor, highlightColor;
  int backgroundColor = color(255);
  // these define a color palette, that can be used in different ways.
  // use foreColor() and bkgndColor() for drawing.
  float indicatorWidth;
  boolean active = false;
  boolean pressed = false;
  boolean hidden = false;
  boolean showName = true;
  boolean showVal = true;
  float nameTextSize, valTextSize;
  float leading = 0.8f;
  int decimalPlaces = 2;
  boolean quantized = false;
  float quantizeUnit;
  
  float dataMin, dataMax;
  boolean logScale = false;
  float pos = 0.5f; // current position, 0..1
  
  slider(String name0, float[] pos, int neutral, int actv, float minVal, float maxVal) {
    neutralColor = neutral;
    activeColor = actv;
    highlightColor = darken(activeColor);
    x0 = pos[0]; y0 = pos[1]; width = pos[2]; height = pos[3];
    indicatorWidth = height;
    nameTextSize = height;
    valTextSize = 1.2f*height;
    name = name0;
    dataMin = minVal;
    dataMax = maxVal;
  }
  
  public void quantize(float unit) {
    quantized = true;
    quantizeUnit = unit;
    if (abs(unit-round(unit)) < 1e-6f) {
      decimalPlaces = 0;
    }
    setVal(getVal());
  }

  public int foreColor() {
    if (pressed) {
      return(highlightColor);
    } else if (over()) {
      return(activeColor);
    } else {
      return(neutralColor);
    }
  }
  
  public int bkgndColor() {
    return backgroundColor;
  }
  
  public void drawBar() {
    rectMode(CORNER);
    noStroke();
    fill(bkgndColor());
    rect(x0,y0,width,height);
    fill(foreColor());
    float x1 = x0 + (width-indicatorWidth)*getPos();
    rect(x1,y0,indicatorWidth,height);
  }
  
  public void drawName() {
    fill(foreColor());
    textAlign(RIGHT);
    textSize(nameTextSize);
    textLeading(leading*nameTextSize);
    text(name+" ",x0,y0+height);
  }
  
  public void drawVal() {
    fill(foreColor());
    textAlign(LEFT);
    //textSize(valTextSize);
    textSize(14);
    textLeading(leading*valTextSize);
    //text(" "+val2string(getVal()), x0+width, y0+height);  //POT
    //text(" "+val2string(time_day[count]), x0+width, y0+height);
    //text(" "+time_day[count]+"/"+time_month[count]+"/"+time_year[count],  x0+width/5, y0+height+15);
    text(" "+time_day[count]+" "+months[time_month[count]-1]+" "+time_year[count],  x0+width/6, y0+height+15);
  }
  
  public void draw() {
    if (!hidden) {
      drawBar();
      if (showName) {drawName();}
      if (showVal) {drawVal();}
    }
  }
  
  public boolean over() {
    return (((mouseX>=x0) && (mouseX<=x0+width)) && ((mouseY>=y0) && (mouseY<=y0+height)));
  }
  
  public boolean offerMousePress() {
    pressed = over() && (!hidden);
    return pressed;
  }
  
  public boolean update() {
    // returns true if the user is changing the position of the slider.
    boolean result = false;
    if (!hidden) {
      pressed = pressed && mousePressed;
      active = over() && pressed;
      if (active) {
        float newpos = (mouseX - x0) / (width - indicatorWidth);
        if (newpos != getPos()) {
          setPos(newpos);
          result = true;
        }
      }
      draw();
    }
    return result;
  }
  
  // use these four routines for reading & changing the position of the slider.
  // pos = relative position, 0..1
  // val = value in data units  
  public float getPos() {return pos;}
  public float getVal() {return pos2val(getPos());}
  public void setPos(float p) {pos = constrain(p, 0, 1);}
  public void setVal(float v) {setPos(val2pos(v));}

  // these are general conversion functions, that can be used for values other than the current one
  // e.g., to find out the current min and max values allowed, use pos2val(0) and pos2val(1)
  public String val2string(float v) {
    if (decimalPlaces==0) {
      return str(round(v));
    } else {
      float p = pow(10,decimalPlaces);
      return str(round(v*p)/(float)p);
    }
  }
  
  public float val2pos(float v) {
    float p;
    if (quantized) {
      v = round(v/quantizeUnit)*quantizeUnit;
    }
    if (logScale) {
      p = (log(v)-log(dataMin))/(log(dataMax)-log(dataMin));
    } else {
      p = (v-dataMin)/(dataMax-dataMin);
    }
    return p;
  }
  
  public float pos2val(float p) {
    float v;
    if (logScale) {
      v = dataMin * pow(dataMax/dataMin, p);
    } else {
      v = dataMin + (dataMax-dataMin) * p;
    }
    if (quantized) {
      v = round(v/quantizeUnit)*quantizeUnit;
    }
    return v;
  }  
  
}





class toolbar extends guiElement {
  guiElement[] elements;
  float spacing;
  int length=0; // number of defined elements
  float unoccupiedX0, unoccupiedWidth;
  boolean hidden = false;
  slider lastSliderAdded; // this is a kluge to make the "below" option work: elements[length-1].x0 returns 0 when elements[length-1] is a slider
  guiElement lastUpdated = null;
  
  toolbar(float[] pos, int maxElements) {
    elements = new guiElement[maxElements];
    x0 = pos[0]; y0 = pos[1]; width = pos[2]; height = pos[3];
    spacing = height/2;
    unoccupiedX0 = x0;
    unoccupiedWidth = width;
  }
  
  public guiElement lastAdded() {
    return elements[length-1];
  }
  
  public guiElement find(String nm) {
    guiElement theOne = null;
    boolean found = false;
    for (int i=0; ((i<length) && (!found)); i++) {
      found = nm.equals(elements[i].name);
      if (found) theOne = elements[i];
    }
    return theOne;
  }
  
  public guiElement addElement(guiElement E) {
    if (length < elements.length) {
      length++;
      elements[length-1] = E;
      return E;
    } else {
      return null;
    }    
  }
  
  // to add an element to the toolbar, use one of the following: arguments match the constructors for
  // each class, but replace the position rectangle with "left" or "right."
  public textButton addTextButton(String name0, String alignmt, int neutral, int actv) {
    return (textButton) addElement(new textButton(name0, nextPosition(alignmt,height), neutral, actv));
  }
  
  public polyButton addPolyButton(String name0, String alignmt, int neutral, int actv, float[] xx, float[] yy) {
    return (polyButton) addElement(new polyButton(name0, nextPosition(alignmt,height), neutral, actv, xx, yy));
  }

  public multistatePolyButton addMultistatePolyButton(String name0, int N, String alignmt) {
    return (multistatePolyButton) addElement(new multistatePolyButton(name0, N, nextPosition(alignmt,2.5f*height)));
  }

  public slider addSlider(String name0, String alignmt, int neutral, int actv, float minVal, float maxVal) {
    float ht = height/3;
    float wd = 10*ht;
    float[] pos = nextPosition(alignmt,wd);
    pos[1] += (pos[3]-ht)/2;
    pos[3] = ht;
    slider S = new slider(name0, pos, neutral, actv, minVal, maxVal);
    addElement(S);
    lastSliderAdded = S;
    textSize(S.nameTextSize);
    float nameWidth = textWidth(S.name+" ");
    float valWidth = max(textWidth(S.val2string(S.pos2val(0))+" "), textWidth(S.val2string(S.pos2val(1))+" "));
    if (alignmt.equals("left") || alignmt.equals("LEFT")) {
      S.x0 += nameWidth;
      unoccupiedX0 += (nameWidth + valWidth);
      unoccupiedWidth -= (nameWidth + valWidth);
    } else if (alignmt.equals("right") || alignmt.equals("RIGHT")) {
      S.x0 -= valWidth;
      unoccupiedWidth -= (nameWidth + valWidth);
    } 
    return S;
  }
  
  public float[] unoccupied() {
    float[] r = {unoccupiedX0, y0, unoccupiedWidth, height};
    return r;
  }

  public float[] nextPosition(String alignmt, float dx) {  
    float dxtot = dx;
    float[] r;
    if (alignmt.equals("left") || alignmt.equals("LEFT")) { // in the main toolbar, aligned left
      if (unoccupiedX0 > x0) {dxtot += spacing;} // add spacer unless all the way at the left
      unoccupiedX0 += dxtot;
      unoccupiedWidth -= dxtot;
      r = defineRect(unoccupiedX0 - dx, y0, dx, height);
    } else if (alignmt.equals("right") || alignmt.equals("RIGHT")) { // in the main toolbar, aligned right
      if (unoccupiedX0+unoccupiedWidth < x0+width) {dxtot += spacing;} // add spacer unless all the way at the right
      unoccupiedWidth -= dxtot;
      r = defineRect(unoccupiedX0 + unoccupiedWidth, y0, dx, height);
    } else if (alignmt.equals("below") || alignmt.equals("BELOW")) { // directly below the last element defined
      // note: when the last element is a slider, the line below returns 0
      float xx = elements[length-1].x0;
      float yy = elements[length-1].y0 + elements[length-1].height + spacing;
      if (elements[length-1] instanceof slider) {
        xx = lastSliderAdded.x0;
        yy = lastSliderAdded.y0+1.5f*lastSliderAdded.height;
      }
      r = defineRect(xx, yy, dx, height);
    } else {
      r = defineRect(0,0,0,0);
    }
    return r;
  }
    
  public boolean update() {
    boolean result = false;
    if (!hidden) {
      for (int i=0; i<length; i++) {
        boolean updated = elements[i].update();
        if (updated) {
          lastUpdated = elements[i];
          result = true;
        }
      }
    }
    return result;
  }
  
  public boolean offerMousePress() {
    boolean captured = false;
    for (int i=length-1; ((i>=0) && (!captured)); i=i-1) {
      captured = elements[i].offerMousePress();
    }
    return captured;
  } 
 
}



class dragSelector {
  float awareWidth, awareHeight, awareX0, awareY0; // the screen region that's monitored and selectable
  float selectedWidth, selectedHeight, selectedX0, selectedY0; // the current size of the selected rectangle; when not pressed, stores the last rect selected
  boolean pressed = false;
  boolean aware = true;
  
  dragSelector(float[] pos) {
    setAwareRect(pos);
    selectedX0 = -1; selectedY0 = -1; selectedWidth = -1; selectedHeight = -1;
  }
  
  public void setAwareRect(float[] pos) {
    awareX0 = pos[0]; awareY0 = pos[1]; awareWidth = pos[2]; awareHeight = pos[3];
  }

  public void draw() {
    fill(color(255,255,255,0.2f*255));
    stroke(color(255,255,255));
    rectMode(CORNER);
    rect(selectedX0,selectedY0,selectedWidth,selectedHeight); 
  }
  
  public boolean over() {
    return (((mouseX>=awareX0) && (mouseX<=awareX0+awareWidth)) && ((mouseY>=awareY0) && (mouseY<=awareY0+awareHeight)));
  }
  
  public boolean offerMousePress() {
    pressed = false;
    if (aware) {
      pressed = over();
      if (pressed) {
        selectedX0 = mouseX;
        selectedY0 = mouseY;
        selectedWidth = 0;
        selectedHeight = 0;
      }
    }
    return pressed;
  }
  
  public boolean update() {
    boolean released = false;
    if (aware) {
      pressed = (pressed) && (mousePressed); // already activated and mouse still down?
      if (pressed) {
        float x1 = constrain(mouseX, awareX0, awareX0+awareWidth);
        selectedWidth = x1 - selectedX0;   
        float y1 = constrain(mouseY, awareY0, awareY0+awareHeight);  
        selectedHeight = y1 - selectedY0;
        draw();
      } else {
        released = true;
      }
    }
    return released;
  }
  
  public float[] selection() {
    float[] r = {selectedX0, selectedY0, selectedWidth, selectedHeight};
    return r;
  }
  
}

// version 1.0

// color utilities ---------------------------------------

public int colorblend(int c1, int c2, float r) {
  return color(lerp(red(c1),red(c2),r),lerp(green(c1),green(c2),r),lerp(blue(c1),blue(c2),r),lerp(alpha(c1),alpha(c2),r));
}

public int shift(int col, float r) {
  if (r>0) {
    return colorblend(col,color(255,255,255,alpha(col)),r);
  } else {
    return colorblend(col,color(0,0,0,alpha(col)),-r);  
  }
}

public int lighten(int col) {return shift(col,0.25f);}

public int darken(int col) {return shift(col,-0.25f);}

public int randomshift(int col) {
  return shift(col, random(-0.5f,0.5f));
}

public int colorinterp(float val, float lowval, float highval, int[] cmap) {return colorinterp(val,lowval,highval,cmap,"nearest");}
public int colorinterp(float val, float lowval, float highval, int[] cmap, String mode) {
  if (mode.equals("linear")) {
    float level = constrain((val-lowval)/(highval-lowval),0,1-1e-6f) * cmap.length;
    int level0 = floor(level);
    int level1 = constrain(ceil(level),0,cmap.length-1);
    return colorblend(cmap[level0],cmap[level1],level-level0);
  } else {
    int level = floor(constrain((val-lowval)/(highval-lowval),0,1-1e-6f) * cmap.length);
    return cmap[level];
  }
}

public int[] warmrainbow() {return warmrainbow(20);}
public int[] warmrainbow(int n) {return warmrainbow(n, 0.9f, 0.3f);}
public int[] warmrainbow(int n, float bright, float contrast) {
  // nice colorscale from blue to yellow to red
  float Hblue = 0.55f; // hues for end & middle colors
  float Hyellow = 0.16667f;
  float Hred = 0;
  float dipWidth = 1.5f; // width of the dip in saturation over green
  
  float[] H = new float[n];
  float[] S = new float[n];
  float[] B = new float[n];
  int N = n-1;
  int iy = floor(n/(float)2); // index of yellow, the middle color
  
  // hue
  for (int i=0; i<=iy; i++) {
    H[i] = Hblue - (Hblue-Hyellow) *sq(i/(float)iy);
  }
  for (int i=iy+1; i<n; i++) {
    H[i] = lerp(H[iy],Hred,(i-iy)/((float)n-iy));
  }
  
  //saturation
  // find greenest color
  int ig = 0;
  for (int i=1; i<n; i++) {
    if (abs(H[i]-0.3333f) < abs(H[ig]-0.3333f)) {ig = i;}
  }
  // gaussian dip in saturation
  for (int i=0; i<n; i++) {
    S[i] = 1 - 0.5f*exp(-dipWidth*sq(i/(float)ig-1));
  }
  
  // brightness
  float b = 4*contrast/N;
  float a = -b/N;
  for (int i=0; i<iy; i++) {
    B[i] = bright - lerp(contrast,0,i/((float)iy-1));
  }
  for (int i=iy; i<n; i++) {
    B[i] = a*sq(i) + b*i + bright - contrast;
  }
  
  colorMode(HSB);
  int[] map = new int[n];
  for (int i=0; i<map.length; i++) {
    map[i] = color(H[i]*255,S[i]*255,B[i]*255);
  }
  colorMode(RGB);
  
  return map;
}


// other utilities ---------------------------------------

public float[] defineRect(float x0, float y0, float wd, float ht) {
  float[] r = {x0,y0,wd,ht};
  return r;
}

/*
 * ----------------------------------
 *  Radio Button Class for Processing 2.0
 * ----------------------------------
 *
 * this is a simple radio button class. The following shows 
 * you how to use it in a minimalistic way.
 *
 * DEPENDENCIES:
 *   N/A
 *
 * Created:  April, 12 2012
 * Author:   Alejandro Dirgan
 * Version:  0.14
 *
 * License:  GPLv3
 *   (http://www.fsf.org/licensing/)
 *
 * Follow Us
 *    adirgan.blogspot.com
 *    twitter: @ydirgan
 *    https://www.facebook.com/groups/mmiiccrrooss/
 *    https://plus.google.com/b/111940495387297822358/
 *
 * DISCLAIMER **
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS," AND WE MAKE NO EXPRESS OR IMPLIED WARRANTIES WHATSOEVER 
 * WITH RESPECT TO ITS FUNCTIONALITY, OPERABILITY, OR USE, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR INFRINGEMENT. WE EXPRESSLY 
 * DISCLAIM ANY LIABILITY WHATSOEVER FOR ANY DIRECT, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR SPECIAL 
 * DAMAGES, INCLUDING, WITHOUT LIMITATION, LOST REVENUES, LOST PROFITS, LOSSES RESULTING FROM BUSINESS 
 * INTERRUPTION OR LOSS OF DATA, REGARDLESS OF THE FORM OF ACTION OR LEGAL THEORY UNDER WHICH THE LIABILITY 
 * MAY BE ASSERTED, EVEN IF ADVISED OF THE POSSIBILITY OR LIKELIHOOD OF SUCH DAMAGES.
*/


/*
 this is a simple radio button class. The following shows you how to use it in a minimalistic way.


String[] options = {"First","Second","Third", "Fourth"}; 
ADradio radioButton;
int radio;


PFont output; 

void setup()
{
  size(300,300);
  smooth();
  output = createFont("Arial",24,true);  

  radioButton = new ADradio(117, 78, options, "radioButton"); 
  radioButton.setDebugOn();
  radioButton.setBoxFillColor(#F7ECD4);  
  radioButton.setValue(1);

}

void draw()
{
  background(#FFFFFF);

  radioButton.update();

  textFont(output,24);   
  text(options[radioButton.getValue()], (width-textWidth(options[radioButton.getValue()]))/2, height-20);

}


*/

class ADRadio
{
  
  int externalCircleColor=0xff000000;
  int externalFillCircleColor=0xffFFFFFF;
  int internalCircleColor=0xff000000;
  int internalFillCircleColor=0xff000000;
  
  boolean fillExternalCircle=false;
  
  PFont rText;
  int textColor=0xff000000;
  int textShadowColor=0xff7E7E7E;
  boolean textShadow=false;
  int textPoints=12;
  
  int xTextOffset=20;
  int yTextSpacing=14;
  
  int circleRadius=12;
  float circleLineWidth=0.5f;
 
  float boxLineWidth=0.2f;
  boolean boxFilled=false;
  int boxLineColor=0xff000000;
  int boxFillColor=0xffF4F5D7;
  boolean boxVisible=false;
  
  String[] radioText;
  boolean[] radioChoose; 
  
  int over=0;
  int nC;
  
  int rX, rY;
  
  float maxTextWidth=0;
  
  String radioLabel;
  
  boolean debug=false;
  
  int boxXMargin=5;
  int boxYMargin=5;
  
  int bX, bY, bW, bH;
  boolean pressOnlyOnce=true;
  int deb=0;    
  
///////////////////////////////////////////////////////  
  ADRadio(int x, int y, String[] op, String id)
  {
    rX=x;
    rY=y;
    radioText=op;
    
    nC=op.length;
    radioChoose = new boolean[nC];
        
    rText = createFont("Arial",16,true);      
    textFont(rText,textPoints);   
    textAlign(LEFT);
    
    for (int i=0; i<nC; i++) 
    {
      if (textWidth(radioText[i]) > maxTextWidth) maxTextWidth=textWidth(radioText[i]);
      radioChoose[i]=false;
    }
    
    radioChoose[over]=true;
    
    radioLabel=id;
    
    calculateBox();
    
  }
  
///////////////////////////////////////////////////////  
  public void calculateBox()
  {
    bX=rX-circleRadius/2-boxXMargin;
    bY=rY-circleRadius/2-boxYMargin;
    bW=circleRadius*2+xTextOffset+(int )maxTextWidth;
    bH=radioText.length*circleRadius + (radioText.length-1)*yTextSpacing + boxYMargin*2;
  }  
///////////////////////////////////////////////////////  
  public void setValue(int n)
  {
    if (n<0) n=0;
    if (n>(nC-1)) n=nC-1;
    
   for (int i=0; i<nC; i++) radioChoose[i]=false;
   radioChoose[n]=true;  
   over=n; 
  }
///////////////////////////////////////////////////////  
  public void deBounce(int n)
  {
    if (pressOnlyOnce) 
      return;
    else
      
    if (deb++ > n) 
    {
      deb=0;
      pressOnlyOnce=true;
    }
    
  }  ///////////////////////////////////////////////////////  
  public boolean mouseOver()
  {
    boolean result=false; 
    
    if (debug)
      if ((mouseX>=bX) && (mouseX<=bX+bW) && (mouseY>=bY) && (mouseY<=bY+bH))
      {
        if (mousePressed && mouseButton==LEFT && keyPressed)
        {
          if (keyCode==CONTROL)
          {
            rX=rX+(int )((float )(mouseX-pmouseX)*1);
            rY=rY+(int )((float )(mouseY-pmouseY)*1);
            calculateBox();
          }
          if (keyCode==SHIFT && pressOnlyOnce) 
          {
            printGeometry();
            pressOnlyOnce=false;
          }
          deBounce(5);
          
        }
      }
      
    for (int i=0; i<nC; i++)
    {
      if ((mouseX>=(rX-circleRadius)) && (mouseX<=(rX+circleRadius)) && (mouseY>=(rY+(i*(yTextSpacing+circleRadius))-circleRadius)) && (mouseY<=(rY+(i*(yTextSpacing+circleRadius))+circleRadius)))
      {
        result=true;
        
        if (mousePressed && mouseButton==LEFT && pressOnlyOnce)
        {
          over=i;
          setValue(over);
          pressOnlyOnce=false;
        }
        deBounce(5);
        i=nC;
      }
      else
      {
        result=false;
      }
    } 
    return result;
  }
///////////////////////////////////////////////////////  
  public void drawBox()
  {
    if (!boxVisible) return;
    if (boxFilled)
      fill(boxFillColor);
    else
      noFill();
    strokeWeight(boxLineWidth);
    stroke(boxLineColor);

    rect(bX, bY, bW, bH);

  }  
///////////////////////////////////////////////////////  
  public void drawCircles()
  {
    strokeWeight(circleLineWidth);
    for (int i=0; i<nC; i++)
    {
      if (!fillExternalCircle) 
        noFill();
      else
        fill(externalFillCircleColor);  
      stroke(externalCircleColor);  
      ellipse(rX, rY+(i*(yTextSpacing+circleRadius)), circleRadius, circleRadius);

      fill(internalFillCircleColor);
      stroke(internalCircleColor);  

      if (radioChoose[i])
         ellipse(rX, rY+(i*(yTextSpacing+circleRadius)), circleRadius-8, circleRadius-8);
    }
    mouseOver();
   
  }
///////////////////////////////////////////////////////  
  public void drawText()
  {
    float yOffset=rY+textPoints/3+1;
    stroke(textColor);
    textFont(rText,textPoints);   
    textAlign(LEFT);

    for (int i=0; i<nC; i++)
    {
      if (textShadow)
      {
        stroke(textShadowColor);
        text(radioText[i], rX+xTextOffset+1, yOffset+(i*(yTextSpacing+circleRadius))+1);
        stroke(textColor);
      }
      text(radioText[i], rX+xTextOffset, yOffset+(i*(yTextSpacing+circleRadius)));
    }
    
  }  
  
///////////////////////////////////////////////////////  
  public int update()
  {
    drawBox();
    drawCircles();
    drawText();
    
    return over;
  }

///////////////////////////////////////////////////////  
  public int getValue()
  {
    return over;
  }
 
///////////////////////////////////////////////////////  
  public void setDebugOn()
  {
    debug=true;
  }
///////////////////////////////////////////////////////  
  public void setDebugOff()
  {
    debug=false;
  }
///////////////////////////////////////////////////////  
  public void printGeometry()
  {
    println("radio = new ADradio("+rX+", "+rY+", arrayOfOptions"+", \""+radioLabel+"\");");

  }
///////////////////////////////////////////////////////  
  public void setExternalCircleColor(int c)
  {
    externalCircleColor=c;
  }
///////////////////////////////////////////////////////  
  public void setExternalFillCircleColor(int c)
  {
    externalFillCircleColor=c;
  }
///////////////////////////////////////////////////////  
  public void setInternalCircleColorr(int c)
  {
    externalFillCircleColor=c;
  }
///////////////////////////////////////////////////////  
  public void setInternalFillCircleColor(int c)
  {
    externalFillCircleColor=c;
  }
///////////////////////////////////////////////////////  
  public void setTextColor(int c)
  {
    textColor=c;
  }
///////////////////////////////////////////////////////  
  public void setTextShadowColor(int c)
  {
    textShadowColor=c;
  }
///////////////////////////////////////////////////////  
  public void setShadowOn()
  {
    textShadow=true;
  }
///////////////////////////////////////////////////////  
  public void setShadowOff()
  {
    textShadow=false;
  }
///////////////////////////////////////////////////////  
  public void setTextSize(int s)
  {
    textPoints=s;
  }
///////////////////////////////////////////////////////  
  public void setXTextOffset(int s)
  {
    xTextOffset=s;
  }
///////////////////////////////////////////////////////  
  public void setyTextSpacing(int s)
  {
    yTextSpacing=s;
  }
///////////////////////////////////////////////////////  
  public void setCircleRadius(int s)
  {
    circleRadius=s;
  }
///////////////////////////////////////////////////////  
  public void setBoxLineWidth(int s)
  {
    boxLineWidth=s;
  }
///////////////////////////////////////////////////////  
  public void setBoxLineColor(int c)
  {
    boxLineColor=c;
  }
///////////////////////////////////////////////////////  
  public void setBoxFillColor(int c)
  {
    boxFillColor=c;
    setBoxFilledOn();
  }
///////////////////////////////////////////////////////  
  public void setBoxFilledOn()
  {
    boxFilled=true;
  }
///////////////////////////////////////////////////////  
  public void setBoxFilledOff()
  {
    boxFilled=false;
  }
///////////////////////////////////////////////////////  
  public void setBoxVisibleOn()
  {
    boxVisible=true;
  }
///////////////////////////////////////////////////////  
  public void setBoxVisibleOff()
  {
    boxVisible=false;
  }
///////////////////////////////////////////////////////  
  public void setLabel(String l)
  {
    radioLabel=l;
  }

}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--stop-color=#cccccc", "ECO" });
  }
}
