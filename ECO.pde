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
color c1, c2;
boolean uptake=true;
float acumula=0;
int contador=0;
int radio1_old, radio2_old;
float a1_old=0;

color colorfondo = color(68,169,188);
color colorrelease = color(175,37,81);

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
  
void setup() {
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
       time[index] = float(data[0]);
       lon[index] = float(data[1]);
       lat[index] = float(data[2]);
       ncep[index] = float(data[3]);
       ncep2[index] = float(data[4]);
       hirlam[index] = float(data[5]);
       quikscat[index] = float(data[6]);
       era[index] = float(data[7]);
       grad[index] = float(data[11]);
       sCO2[index] = float(data[12]);
       k600[index] = float(data[13]);
       k660[index] = float(data[14]);
       route[index] = int(data[15]);
       time_day[index] = int(data[16]);
       time_month[index] = int(data[17]);
       time_year[index] = int(data[18]);
     }
   }  

  //Set up GUI controls
  float[] pos = {
    5, 140, width-20, 40
  };
  color gray1 = #2E4F55;
  color gray2 = darken(gray1);
  color gray3 = darken(gray2);
  tools = new toolbar(pos, 10);
  tools.addSlider("Time", "left", gray2, gray3, 2452602, 2452914);
  ((slider) tools.lastAdded()).setVal(2452603);
 
  //Add radio button to winds
  radioButton = new ADRadio(width-280, 70, options, "radioButton");
  radioButton.setDebugOn();
  radioButton.setBoxFillColor(#F7ECD4); 
  radioButton.setValue(1);
  radio1_old=radioButton.getValue();
  
  //Add radio button to algorithms
  radioButton2 = new ADRadio(width-150, 70, options2, "radioButton");
  radioButton2.setDebugOn();
  radioButton2.setBoxFillColor(#F7ECD4); 
  radioButton2.setValue(3);
  radio2_old=radioButton2.getValue();
  
  //Figure with the sea-air interface;
  c2=darken(darken(darken(colorfondo)));
  c1 = darken(colorfondo); 
  
  //Points will be plotted regularly along the route to allow comparisons
  for(int index2 = 0; index2<=500; index2 = index2 + 1) { 
    latroute[index2]=42.25+0.01*index2;
    FCO2[index2]=0;
  }
}

void draw() {
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
  
  //Las latitudes van aprox. desde 42.25 hasta 47.25ºN
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
    if(route[index]==route[count] && lat[index]>=42.25 && lat[index]<=47.25){
     for(int index2 = 0; index2<=500; index2 = index2 + 1) {      
       if(abs(lat[index]-latroute[index2])<=0.01 && wind[index]!=-9999 && k600[index]!=-9999 && k660[index]!=-9999 && sCO2[index]!=-9999 && grad[index]!=-9999 && grad[index]<250) {       
           //And the algorithm?
           if(radioButton2.getValue() == 0) {
              k=(0.222*pow(wind[index],2)+0.333*wind[index])*sqrt(k600[index]);
           } else if (radioButton2.getValue() == 1) {
              k=(0.266*pow(wind[index],2))*sqrt(k600[index]);
           } else if (radioButton2.getValue() == 2) {
              k=(0.27*pow(wind[index],2))*sqrt(k660[index]);
           } else if (radioButton2.getValue() == 3) {
              k=(0.31*pow(wind[index],2))*sqrt(k660[index]);   
           } else if (radioButton2.getValue() == 4) {
              if(wind[index]<=3.6) {
                k=0.17*wind[index]*pow(k600[index],2/3);
              } else if (wind[index]>3.6 & wind[index]<=13.0) {
                k=(2.85*wind[index]-9.65)*sqrt(k600[index]);
              } else {
                k=(5.9*wind[index]-49.3)*sqrt(k600[index]);   
              }  
            }   
           FCO2[index2] = 0.08766 * k * sCO2[index] * grad[index];
           //print(FCO2[index2] + " "); 
         } 
      }
   }
 }
  change=false;
} //end if frame=0  



  strokeWeight(0.5);
  for(int index2 = 0; index2<=500; index2 = index2 + 1) {      
     if(uptake && FCO2[index2]>0 && FCO2_old[index2]>=0) {    
           if(latroute[index2]>=42.25 && latroute[index2]<=42.8) {
             float desfase_x = width_route_1*(latroute[index2]-42.25)/(42.8-42.25);
             float desfase_y = height_route_1*(latroute[index2]-42.25)/(42.8-42.25);
             stroke(colorfondo);  
             line(vigo_x-desfase_x,vigo_y-desfase_y,vigo_x-desfase_x,vigo_y-desfase_y-(FCO2_old[index2]+(FCO2[index2]-FCO2_old[index2])*index_frate/60)*10);
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-FCO2_old[index2]*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) {  
               stroke(216,207,22,256-(index_frate2-60)*256/60);    
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-FCO2_old[index2]*10,1,1);          
             }               
           } else if(latroute[index2]>42.8 && latroute[index2]<=43.2) {
             float desfase_x = width_route_2*(latroute[index2]-42.8)/(43.2-42.8);
             float desfase_y = height_route_2*(latroute[index2]-42.8)/(43.2-42.8);  
             stroke(colorfondo);        
             line(codo1_x+desfase_x,codo1_y-desfase_y,codo1_x+desfase_x,codo1_y-desfase_y-(FCO2_old[index2]+(FCO2[index2]-FCO2_old[index2])*index_frate/60)*10);        
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-FCO2_old[index2]*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-FCO2_old[index2]*10,1,1);
             }            
           } else if(latroute[index2]>43.2 && latroute[index2]<47.25) {  
             float desfase_x = width_route_3*(latroute[index2]-43.2)/(47.25-43.2);
             float desfase_y = height_route_3*(latroute[index2]-43.2)/(47.25-43.2);   
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
             if(latroute[index2]>=42.25 && latroute[index2]<=42.8) {
             float desfase_x = width_route_1*(latroute[index2]-42.25)/(42.8-42.25);
             float desfase_y = height_route_1*(latroute[index2]-42.25)/(42.8-42.25);
             stroke(colorrelease);   
             line(vigo_x-desfase_x,vigo_y-desfase_y,vigo_x-desfase_x,vigo_y-desfase_y-(abs(FCO2_old[index2])+(abs(FCO2[index2])-abs(FCO2_old[index2]))*index_frate/60)*10);
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) {  
               stroke(216,207,22,256-(index_frate2-60)*256/60);    
               ellipse(vigo_x-desfase_x,vigo_y-desfase_y-abs(FCO2_old[index2])*10,1,1);          
             }                     
            } else if(latroute[index2]>42.8 && lat[index2]<=43.2) {
             float desfase_x = width_route_2*(latroute[index2]-42.8)/(43.2-42.8);
             float desfase_y = height_route_2*(latroute[index2]-42.8)/(43.2-42.8);
             stroke(colorrelease);   
             line(codo1_x+desfase_x,codo1_y-desfase_y,codo1_x+desfase_x,codo1_y-desfase_y-(abs(FCO2_old[index2])+(abs(FCO2[index2])-abs(FCO2_old[index2]))*index_frate/60)*10);   
             if(index_frate2<60 && FCO2_old[index2]!=0) {
               stroke(216,207,22,index_frate2*256/60);
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             } else if (index_frate2>=60 && index_frate2<120 && FCO2_old[index2]!=0) { 
               stroke(216,207,22,256-(index_frate2-60)*256/60);  
               ellipse(codo1_x+desfase_x,codo1_y-desfase_y-abs(FCO2_old[index2])*10,1,1);
             }       
           } else if(latroute[index2]>43.2 && latroute[index2]<47.25) {  
             float desfase_x = width_route_3*(latroute[index2]-43.2)/(47.25-43.2);
             float desfase_y = height_route_3*(latroute[index2]-43.2)/(47.25-43.2);
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



void mousePressed() {
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

void annotate() {
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


