import java.awt.image.*;
//import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

//import cs1.Keyboard;
public class Compute {
	//a few general notes to anyone brave enough to try and read this program:
	// first of all, any single character variables are USUALLY used in wither loops or parameters, in for loops the first
	//level character is almost always i, the second level is almost always j and if there is a third level I try and stick with k
	// when I do x,y coordinates in loops, the first level is always x, then the second is always y
	//there are some parts of this program that are written really badly, and that's my fault, it's slow and inconsistent, and 
	//some things need to be fixed, for there is always things that need to be fixed
	//as a note to users, I suggest using smaller images with this, it'll work on big images, but slowly, if you have a
	//multi-core processor and a fair amount of ram this is less of an issue, because java tends to only use one core
	//I will however warn people using computers with less ram that java will always try and take like 2.5 GBs of RAM, and if it
	//needs more than that it will go over to disk, which will really slow stuff down
	int height; // the height variable is an integer that is used to record the height of the image in pixels, i.e. the y axis
	int width; //similar to height, this variable is a measure of width in pixels, i.e. the x axis
	int threshold; // this variable is another user entered variable, but this one is the RGB value of the area in question
	ArrayList<ArrayList<Point>> pic = new ArrayList<ArrayList<Point>>(); //this is ArrayList of ArrayLists that stores the image information
	//once it has been read in in the acquisition subroutine, the ArrayList itself is the X axis in a sense, and all the actual
	//image information is stored in the ArrayLists that are within this one
	ArrayList<Point> remove = new ArrayList<Point>(); //this is an ArrayList of Point objects that are flagged by the trimming
	//method as those that should be removed. they are later removed when the writepixels method is called
	ArrayList<Point> area = new ArrayList<Point>();//this arrayList is an arrayList of all pixels that are within the area, in other
	//words pixels that have a value equal to the threshold, which is less of a threshold as it is an exact value
	//	private boolean secondInLset=false;
	@SuppressWarnings("rawtypes")
	ArrayList border = new ArrayList(); //this is an ArrayList that is populated by the borderID method and it is a list of all
	//points that are on the border, in other words pixels that are in area and adjacent to a pixel that is not the threshold
	public Compute(){
		//creates object and generates an ArrayList of the specified dimensions
		//		height=h;
		//		width=w;
		remove = new ArrayList<Point>();
	}
	public Point getPoint(int x, int y){
		return (Point) (pic.get(x).get(y));
	}
	//the following was a constructor that could be called without height or width data, but was removed when it was deemed useless
	//	public Compute(boolean test){ 
	//		if (test){
	//			System.out.println("it appears you have called the testing constructor, THIS IS ONLY FOR TESTING \n if you get this" +
	//					" and you weren't trying to enter testing mode, please terminate now");
	//		}else{
	//			System.out.println("wait, why are you even here then?");
	//		}
	//	}
	public ArrayList<LPoint> LsetO(int t, String n, boolean debug){
		Ellipse shape = new Ellipse();
		double sumForces = 1;
		double sumForcesLast = 2;
		ArrayList<LPoint> loop = shape.getEllipse(width, height, 1, 1);
		ArrayList<LPoint> tloop;
		if(debug)System.out.println(loop.size());
		do{
			tloop = loop;
			loop = shape.calcForces(loop, pic, new Point(width/2, height/2), 1, t, .1,  height,width, area);
			shape.removeDuplicatesL(loop);
			//setRemove(loop);
			//writepixels(-1, n);
			sumForcesLast = sumForces;
			sumForces = shape.sumForces(tloop);
			if(debug)System.out.println(loop.size());
		}while (Math.abs(sumForcesLast - sumForces) >=.001);
		return loop;
	}
	public ArrayList<LPoint> LsetO(int t, String n, ArrayList<LPoint> loop, String writeName, boolean debug){
		Ellipse shape = new Ellipse();
		double sumForces = 1;
		double sumForcesLast = 2;
		//ArrayList<LPoint> loop = shape.getEllipse(width, height, 1, 1);
		ArrayList<LPoint> tloop;
		//System.out.println(loop.size());
		do{
			tloop = loop;
			loop = shape.calcForces(loop, pic, new Point(width/2, height/2), .75, t, .1,  height,width, area,4);
			shape.removeDuplicatesL(loop);
			//shape.gapFillL(loop, debug);
			//setRemove(loop);
			//writepixels(-1, n, writeName);
			sumForcesLast = sumForces;
			sumForces = shape.sumForces(tloop);
		}while (Math.abs(sumForcesLast - sumForces) >=.001);
		return loop;
	}
	public ArrayList<Point> expandToFill(int x , int y){
		Ellipse shape = new Ellipse();
		return shape.expandToFill(pic, new LPoint(x,y) , threshold);

	}
	public ArrayList<LPoint> Lset( int t, String n, ArrayList<LPoint> AL, boolean debug, double resolution, String writeName){
		for(int i = 0; i<AL.size(); i++){AL.get(i).setDX(AL.get(i).getx());AL.get(i).setDY(AL.get(i).gety());}
		Ellipse shape = new Ellipse();
		double Energy = Double.MAX_VALUE;
		double EnergyLast = 2;
		ArrayList<LPoint> loop = AL;
		//ArrayList<LPoint> tloop;
		do{
			for(int i = 0; i < loop.size(); i ++){
				loop = shape.movePoints(loop, pic, t, i, resolution);
			}
			EnergyLast = Energy;
			Energy = shape.calcEnergy(loop, pic,  threshold);
			//setRemove(loop);
			//if(secondInLset){writepixels(-1, n, writeName);}
			if(debug){System.out.println("in Lset loop " + Energy);}
		}while(Energy < EnergyLast);
		for(int i = 0; i<AL.size(); i++){AL.get(i).setx(Math.round((float)AL.get(i).getDX()));AL.get(i).sety(Math.round((float)AL.get(i).getDY()));}
		shape.gapFillL(loop, debug);// wait, what? it works now????? no way, that's cray!
		//	secondInLset = true;
		return loop;
	}
	public ArrayList<LPoint> ellipseFit(double step){
		//code for running a very basic level set will go here, it works with the ellipse class to essentially start an eclipse 
		//at the borders of the image and just have it fall in, some key values are a, b, which represent x and y scalar changes respectively
		Ellipse loop = new Ellipse();
		double a = 1;
		double b = 1;
		//double lastEnergy = Double.MAX_VALUE;
		double newEnergy = 0;
		double newEnergyTemp = 0;
		int addType = 0;
		int addNum = 0; double Tstep = step;
		ArrayList<LPoint> ellipse = new ArrayList<LPoint>();
		//do{
		for(int j = 0 ; j < 1000; j++){
			newEnergy = Double.MAX_VALUE;
			for(int i = 0; i<3; i++){
				ellipse = loop.getEllipse(width, height, a, b+((1-i)*Tstep));
				newEnergyTemp = loop.getEnergy(ellipse, pic, new Point(width/2, height/2), 100, 3, 10, a, b, threshold);//may want to change this to accept user inputs
				if(newEnergyTemp < newEnergy){ newEnergy = newEnergyTemp;addNum = i; addType = 1;}
				//but for now I'm just testing so straight values it is
				ellipse = loop.getEllipse(width, height, a+((1-i)*Tstep), b);
				newEnergyTemp = loop.getEnergy(ellipse, pic, new Point(width/2, height/2), 100, 3, 10, a, b, threshold);
				if(newEnergyTemp < newEnergy){ newEnergy = newEnergyTemp;addNum = i; addType = 2;}
				ellipse = loop.getEllipse(width, height, a+((1-i)*Tstep), b+((1-i)*Tstep));
				newEnergyTemp = loop.getEnergy(ellipse, pic, new Point(width/2, height/2), 100, 3, 10, a, b, threshold);
				if(newEnergyTemp < newEnergy){ newEnergy = newEnergyTemp;addNum = i; addType = 3;}

			}
			if (Tstep<= 10*step){
				Tstep += step;
			}
			if(addType == 3){
				a+= ((1-addNum)*Tstep);
				b+= ((1-addNum)*Tstep);
				Tstep = step;
			}else if(addType == 2){
				a+= ((1-addNum)*Tstep);
				Tstep = step;
				//b+= ((1-addNum)*step);
			}else if(addType == 1){
				//a+= ((1-addNum)*step);
				Tstep = step;
				b+= ((1-addNum)*Tstep);
			}
			System.out.println("the fitter is on step " + j);
		}
		//}while(newEnergy<lastEnergy);

		ellipse = loop.getEllipse(width, height, a, b);
		return ellipse;
	}
	public ArrayList<Integer> pixelGetF(String n, int s){
		ArrayList<ArrayList<Integer>> values = new ArrayList<ArrayList<Integer>>();
		BufferedImage img = null; //creates buffered image object that can sture an image from the ImageIO.read() command
		try {
			img = ImageIO.read(new File(n)); //reading the image in itself from the File of name n
		}catch (IOException e) {
			//catches read errors and kills the method if it cannot continue
			System.out.println(e.toString());
			return null;
		}
		int x;
		int y;
		for(int i = 1; i<=10000; i++){
			boolean inYet = false;
			//essentially this part here will take the value that is being read in and add it the the level 2 ArrayList whose
			//first term is equivalent to the value, it's a quick and dirty way of see how many pixels there are of any type
			// and I'll probably change it as it is so freaking slow
			x = (int)((Math.random()*width));
			y = (int)((Math.random()*height));
			for(int k = 0; k<values.size(); k++){
				if (((Integer)values.get(k).get(0)) == img.getRGB(x, y)){
					values.get(k).add(img.getRGB(x, y));
					inYet = true;
				}
			}
			if(!inYet){
				//this part is called if in the ArrayList values there is no element whose first term is the same as the one being 
				//tested, adding a new ArrayList
				System.out.println("x coordinate: " +x+ " y coordinate: " + y + " color value: " +  img.getRGB(x, y));
				//Keyboard.readString();
				ArrayList<Integer> z = new ArrayList<Integer>();
				z.add(img.getRGB(x,y));
				values.add(z);

				inYet = false; // inYet is just a boolean that is set to true if there is no need to make a new ArrayList
			}
		}
		double pc = ((double)s)/100.; //turning the parameter s (n was taken) into a decimal
		ArrayList<Integer> colorFinal = new ArrayList<Integer>(); //had to make an ArrayList that was Integers so I made a new one
		for(int i = 0; i< values.size();i++){
			if(((double)values.get(i).size()/((10000.))) >= pc ){
				colorFinal.add((Integer) values.get(i).get(0));
				//just says that if a certain value represents more than the specified % it is added to the list that will be returned
			}
		}
		return colorFinal;

	}
	public int pixelGetFD(String n, int s){
		ArrayList<ArrayList<Integer>> values = new ArrayList<ArrayList<Integer>>();

		BufferedImage img = null; //creates buffered image object that can sture an image from the ImageIO.read() command
		try {
			img = ImageIO.read(new File(n)); //reading the image in itself from the File of name n
		}catch (IOException e) {
			//catches read errors and kills the method if it cannot continue
			System.out.println(e.toString());
			return 0;
		}
		int x;
		int y;
		for(int i = 1; i<=10000; i++){
			boolean inYet = false;
			//essentially this part here will take the value that is being read in and add it the the level 2 ArrayList whose
			//first term is equivalent to the value, it's a quick and dirty way of see how many pixels there are of any type
			// and I'll probably change it as it is so freaking slow
			x = (int)((Math.random()*width));
			y = (int)((Math.random()*height));
			for(int k = 0; k<values.size(); k++){
				if (((Integer)values.get(k).get(0)) == img.getRGB(x, y)){
					values.get(k).add(img.getRGB(x, y));
					inYet = true;
				}
			}
			if(!inYet){
				//this part is called if in the ArrayList values there is no element whose first term is the same as the one being 
				//tested, adding a new ArrayList
				System.out.println("x coordinate: " +x+ " y coordinate: " + y + " color value: " +  img.getRGB(x, y));
				//Keyboard.readString();
				ArrayList<Integer> z = new ArrayList<Integer>();
				z.add(img.getRGB(x,y));
				values.add(z);

				inYet = false; // inYet is just a boolean that is set to true if there is no need to make a new ArrayList
			}
		}
		double pc = ((double)s)/100.; //turning the parameter s (n was taken) into a decimal
		ArrayList<Integer> colorFinal = new ArrayList<Integer>(); //had to make an ArrayList that was Integers so I made a new one
		for(int i = 0; i< values.size();i++){
			if(((double)values.get(i).size()/((10000.))) >= pc ){
				colorFinal.add((Integer) values.get(i).get(0));
				//just says that if a certain value represents more than the specified % it is added to the list that will be returned
			}
		}
		for(int i = 0 ; i< values.size();i++){
			if(img.getRGB(1,1) == values.get(i).get(0)){
				values.remove(i);
			}
		}
		double min= Double.MAX_VALUE;
		int index = 0;
		for(int i = 0; i < values.size(); i ++){
			if(((double)values.get(i).size()/((10000.))<min)){
				index = i;
				min = ((double)values.get(i).size()/((10000.)));
			}
		}



		return values.get(index).get(0);

	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean knitter(){
		Point tP = new Point(-1,-1);
		int i = 0;
		int originalSize = border.size();
		ArrayList<Point> endsTemp = new ArrayList<Point>();
		while (i< border.size()){
			//this loop goes through the entire border ArrayList and picks out all the end points, then adds them to the list endsTemp
			//System.out.println(endsTemp.size());
			if(border.get(i).getClass().equals(tP.getClass())){
				tP = (Point)border.get(i);
				tP.setSegment(i);
				tP.setStart(true);
				endsTemp.add(tP);
			} else if(border.get(i).getClass().equals(area.getClass())){
				tP = (Point)((((ArrayList)( border.get(i)))).get(0));
				tP.setSegment(i);
				tP.setStart(true);
				endsTemp.add(tP);
				tP = (Point)((((ArrayList)( border.get(i)))).get(((ArrayList<ArrayList>) border.get(i)).size() - 1));
				tP.setSegment(i);
				tP.setStart(false);
				endsTemp.add(tP);
			}
			i++;
		}

		//oh my goodness this section is silly
		//this ArrayList will store the indexes of ArrayLists that need to be cut out of the border ArrayList as they have been added
		//to other ArrayLists with the arrayAdder method
		//ArrayList removal = new ArrayList(); NOT USED ANY MORE, found a better solution
		//gotAny is a boolean variable that is set to true every time two different fragments are joined, continuing the while loop
		//until there is an iteration where no ends are joined
		boolean gotAny = true;
		//endGotAny just returns whether any segment were joined
		boolean endGotAny = false;
		Integer I = new Integer(42);
		while(gotAny){
			//double s = endsTemp.size();
			gotAny = false;
			for(i = 0; i<endsTemp.size(); i++){
				for(int j = 0; j<endsTemp.size();j++){
					if(border.get(endsTemp.get(j).getSegment()).getClass().equals(I.getClass())){
						//do nothing, yay!
					}else if(isAdjacent(endsTemp.get(i), endsTemp.get(j))){
						// this series of if statements are testing to see if the points that have been identified as adjacent
						//are at the beginning or end of their respective lists and then is fixing the other end points of that
						//segment so that it all still works
						if((border.get(endsTemp.get(i).getSegment()).getClass().equals(tP.getClass())) &&
								(border.get(endsTemp.get(j).getSegment()).getClass().equals(tP.getClass()))){
							ArrayList<Point> t = new ArrayList<Point>();
							t.add(endsTemp.get(i));
							t.add(endsTemp.get(j));
							border.set(endsTemp.get(i).getSegment(), t);
							//border.set(endsTemp.get(j).getSegment(), -99);
							endsTemp.get(j).setStart(false);
							endsTemp.get(j).setSegment(endsTemp.get(i).getSegment());
						}else if ((border.get(endsTemp.get(i).getSegment()).getClass().equals(tP.getClass())) &&
								!(border.get(endsTemp.get(j).getSegment()).getClass().equals(tP.getClass()))){
							if(endsTemp.get(j).getStart()){
								(((ArrayList<ArrayList<Point>>)(border)).get(endsTemp.get(j).getSegment())).add(0, endsTemp.get(i));
							}else{
								(((ArrayList<ArrayList>)(border)).get(endsTemp.get(j).getSegment())).add(
										(((ArrayList<ArrayList>)(border)).get(endsTemp.get(j).getSegment())).size() -1,
										endsTemp.get(i));
								// i would just like to point out how insane this single line of code is, it's almost impressive, but
								//mostly just dumb
							}
						}else if ((border.get(endsTemp.get(j).getSegment()).getClass().equals(tP.getClass())) &&
								!(border.get(endsTemp.get(i).getSegment()).getClass().equals(tP.getClass()))){
							if(endsTemp.get(i).getStart()){
								(((ArrayList<ArrayList>)(border)).get(endsTemp.get(i).getSegment())).add(0, endsTemp.get(j));
							}else{
								(((ArrayList<ArrayList>)(border)).get(endsTemp.get(i).getSegment())).add(
										(((ArrayList<ArrayList>)(border)).get(endsTemp.get(i).getSegment())).size() -1,
										endsTemp.get(j));
							}
						}else if((endsTemp.get(i).getStart() && endsTemp.get(j).getStart())){
							for(int k = 0; k<endsTemp.size();k++){
								//if(endsTemp.get(k).getSegment() == endsTemp.get(j).getSegment()){
								//endsTemp.get(k).setStart(!(endsTemp.get(j).getStart()));
								//}else 
								if(endsTemp.get(k).getSegment() == endsTemp.get(i).getSegment()){
									endsTemp.get(k).setStart(true);
								}
							}
							//endsTemp.get(j).setStart(!(endsTemp.get(j).getStart()));
							arrayReverser(((ArrayList<ArrayList>)(border).get(endsTemp.get(i).getSegment())));
						} else if((!endsTemp.get(i).getStart() && !endsTemp.get(j).getStart())){
							for(int k = 0; k<endsTemp.size();k++){
								if(endsTemp.get(k).getSegment() == endsTemp.get(j).getSegment()){
									endsTemp.get(k).setStart(false);
								}
							}
							arrayReverser((ArrayList<ArrayList>)(border.get(endsTemp.get(j).getSegment())));
						} else if((endsTemp.get(i).getStart() && !endsTemp.get(j).getStart())){
							for(int k = 0; k<endsTemp.size();k++){
								if(endsTemp.get(k).getSegment() == endsTemp.get(j).getSegment()){
									endsTemp.get(k).setStart(false);
								}else if(endsTemp.get(k).getSegment() == endsTemp.get(i).getSegment()){
									endsTemp.get(k).setStart(true);
								}
							}
							arrayReverser((ArrayList)(border.get(endsTemp.get(j).getSegment())));
							arrayReverser((ArrayList)(border.get(endsTemp.get(i).getSegment())));
						} else if((!endsTemp.get(i).getStart() && endsTemp.get(j).getStart())){
							//and here i guess nothing needs to be done
						}
						if(!(border.get(endsTemp.get(j).getSegment()).getClass().equals(tP.getClass())) &&
								!(border.get(endsTemp.get(i).getSegment()).getClass().equals(tP.getClass()))){
							arrayAdder((ArrayList)(border.get(endsTemp.get(i).getSegment())),
									(ArrayList)(border.get(endsTemp.get(j).getSegment())));
						}
						border.set(endsTemp.get(j).getSegment(), -99);
						gotAny = true;
						endGotAny = true;
						//if(((double)(100.0)*(((double)((double)i)/s))) )
						//System.out.println("knit two points, " + ((double)(100.0)*(((double)((double)i)/s))) + "% done " + i);
						if(i<endsTemp.size()){ endsTemp.remove(i);}
						if(j<endsTemp.size()){endsTemp.remove(j);}
					}
				}

			}
			//this just goes through and takes out all the lists that were added to other lists
			for(i = 0; i < border.size(); i++){
				if(border.get(i).equals(-99)){
					border.remove(i);
					i--;
				}
			}
		}
		System.out.println("The method that connects end points took the number of edges from " + originalSize + " down to " + border.size());
		//Keyboard.readString();
		return endGotAny;
	}
	//the next few methods are just some util stuff about ArrayLists that I need in order for the knitter to work, essentially
	//I could have put all of these on the inside of the knitter but I didn't want it to get long and unweildy
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList arrayAdder(ArrayList a, ArrayList b){
		int j = b.size();
		for(int i = 0 ; i < j;i++){
			a.add(b.get(i));
			//System.out.println(a.size());
			//System.out.println(b.size());
		}
		return a;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList arrayReverser(ArrayList a){
		ArrayList temp = new ArrayList();
		for(int i = a.size() -1; i>=0; i--){
			temp.add(a.get(i));
		}
		return temp;
	}
	public boolean isAdjacent(Point a, Point b){
		int dX = a.getx() - b.getx();
		int dY = a.gety() - b.gety();
		if((dX == 0 && (dY == 1 || dY == -1)) || (dX == 1 && (dY == 1 || dY == -1)) || (dX == -1 && (dY == 1 || dY == -1))){
			return true;
		}else if((dY == 0 && (dX == 1 || dX == -1)) ||(dY == 1 && (dX == 1 || dX == -1)) ||(dY == -1 && (dX == 1 || dX == -1))){
			return true;
		}
		return false;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Integer> pixelGet(String n, int s){
		//simple method that will return all of the rgb values present in the image
		ArrayList<ArrayList> values = new ArrayList<ArrayList>();
		BufferedImage img = null; //creates buffered image object that can sture an image from the ImageIO.read() command
		try {
			img = ImageIO.read(new File(n)); //reading the image in itself from the File of name n
		}catch (IOException e) {
			//catches read errors and kills the method if it cannot continue
			System.out.println(e.toString());
			return null;
		}
		for (int i = 0; i<width;i++){
			for (int j = 0; j<height; j++){
				boolean inYet = false;
				//essentially this part here will take the value that is being read in and add it the the level 2 ArrayList whose
				//first term is equivalent to the value, it's a quick and dirty way of see how many pixels there are of any type
				// and I'll probably change it as it is so freaking slow
				for(int k = 0; k<values.size(); k++){
					if (((Integer)values.get(k).get(0)) == img.getRGB(i, j)){
						values.get(k).add(img.getRGB(i, j));
						inYet = true;
					}
				}
				if(!inYet){
					//this part is called if in the ArrayList values there is no element whose first term is the same as the one being 
					//tested, adding a new ArrayList
					//System.out.println(i+ " " + j + img.getRGB(i, j));
					//Keyboard.readString();
					ArrayList<Integer> y = new ArrayList<Integer>();
					y.add(img.getRGB(i,j));
					values.add(y);
				}
				inYet = false; // inYet is just a boolean that is set to true if there is no need to make a new ArrayList
			}
		}
		double pc = ((double)s)/100.; //turning the parameter s (n was taken) into a decimal
		ArrayList<Integer> colorFinal = new ArrayList<Integer>(); //had to make an ArrayList that was Integers so I made a new one
		for(int i = 0; i< values.size();i++){
			if(((double)values.get(i).size()/((double)height*(double)width)) >= pc ){
				colorFinal.add((Integer) values.get(i).get(0));
				//just says that if a certain value represents more than the specified % it is added to the list that will be returned
			}
		}
		return colorFinal;

	}
	public void setRemove(ArrayList<LPoint> a){
		remove = new ArrayList<Point>();
		for(int i = 0; i<a.size(); i++){
			remove.add(new Point(a.get(i).getx(), a.get(i).gety()));
		}
	}
	public void PsetRemove(ArrayList<Point> a){
		remove = new ArrayList<Point>();
		for(int i = 0; i<a.size(); i++){
			remove.add(new Point(a.get(i).getx(), a.get(i).gety()));
		}
	}
	public void threshold(int threshold, String name, String writeName){
		AreaID(threshold);
		PsetRemove(area);
		writepixels(-1, name, writeName);
		AreaExclude(threshold);
		PsetRemove(area);
		writepixels(-16777213, name, writeName);
		//sometime you just don't want to deal with pixels that are like one one millionth of a value away from each other

	}
	public boolean writepixels(int v, String n){
		int value = v; //this value is value that the pixels are removed will be written as
		BufferedImage img = null; //reading in the image again, boring
		try {
			img = ImageIO.read(new File(n));
		}catch (IOException e) {
			//System.out.println(e.toString());
			return true; //this method returns false if there are no problems, but like here if there is an IO error it will return
			//true and then the main program will tell the user and quietly slinks off to die
		}
		while (remove.size()>0){
			//takes each element of the remove list, goes into the picture and sets it to value, removing the element after it's done
			img.setRGB(remove.get(0).getx(),remove.get(0).gety(), value);
			pic.get(remove.get(0).getx()).get(remove.get(0).gety()).setv(v);
			remove.remove(0);
		}
		//n += "smoothed"; // I didn't want the new image to overwrite the original, so the name is modified
		File image = new File(n); //just a generic file needed in the ImageIO.write() method
		//System.out.println("\"Solved\" an image \nplease note that at this time images are being written as .png files");
		try{
			ImageIO.write(img, "png", image);
		} catch (IOException e){
			return true;//same as above
		}
		return false;//yay, this gets returned if everything goes swimmingly
	}
	public boolean writepixels(int v, String nRead, String nWrite){
		for(int i = 0; i < pic.size(); i++){
			for(int j = 0 ; j < pic.get(0).size(); j++){
				pic.get(i).get(j).rgbToInt();
			}
		}
		if(nWrite == nRead){
			return writepixels(v,nRead);
		}
		int value = v; //this value is value that the pixels are removed will be written as
		BufferedImage img = null; //reading in the image again, boring
		try {
			img = ImageIO.read(new File(nRead));
			ImageIO.write(img, "png", new File(nWrite));
			img = ImageIO.read(new File(nWrite));
		}catch (IOException e) {
			//System.out.println(e.toString());
			return true; //this method returns false if there are no problems, but like here if there is an IO error it will return
			//true and then the main program will tell the user and quietly slinks off to die
		}
		while (remove.size()>0){
			//takes each element of the remove list, goes into the picture and sets it to value, removing the element after it's done
			img.setRGB(remove.get(0).getx(),remove.get(0).gety(), value);
			pic.get(remove.get(0).getx()).get(remove.get(0).gety()).setv(v);
			remove.remove(0);
		}
		//n += "smoothed"; // I didn't want the new image to overwrite the original, so the name is modified
		File image = new File(nWrite); //just a generic file needed in the ImageIO.write() method
		//System.out.println("\"Solved\" an image \nplease note that at this time images are being written as .png files");
		try{
			ImageIO.write(img, "png", image);
		} catch (IOException e){
			return true;//same as above
		}
		return false;//yay, this gets returned if everything goes swimmingly
	}
	public boolean convertJPG(String name){
		// this method should get an image, and take all of its data and put it into the pic
		// ArrayList, returning the boolean true if an error is thrown
		BufferedImage img = null; //image reading, snore!
		try {
			img = ImageIO.read(new File(name));
		}catch (IOException e) {
			System.err.print(e);
			return true;//this boolean is the same as the one in the above method
		}
		int x = 0;
		boolean inside = true;
		while(inside){
			try{
				img.getRGB(x,1);
			}catch(Exception e){
				inside = false;
			}
			x++;
		}
		x--;
		int y = 0;
		inside = true;
		while(inside){
			try{
				img.getRGB(1,y);
			}catch(Exception e){
				inside = false;
			}
			y++;
		}
		y--;
		for(int i=0; i<x;i++){ // first level for loop that sets up the pic array from the user input height and width new ArrayList<Point>();
			pic.add(new ArrayList<Point>());
		}
		width = x;
		height = y;
		for (int i = 0; i<width;i++){
			for (int j = 0; j<height; j++){
				pic.get(i).add(new Point( i ,j ,img.getRGB(i, j)));
				//int tempI = img.getRGB(i, j);
				//System.out.println(tempI + " " + ((tempI >> 16) & 0xff) + " " + ((tempI>>8) &0xff )+ " " +( (tempI) & 0xff));
				//System.out.println(img.getRGB(i, j));
				//this is hard to read, but the method is going into pic, getting the array for
				// the given value i, then adding one of the pixels to it in a Point object
			}
		}
		//even I'm not 100% sure what this code was doing here, I think it was just to test the write command???
		//		File outputFile = new File(name );
		//		try{
		//		ImageIO.write(img, "jpg", outputFile); //need to switch this out of jpg
		//		} catch (IOException e) {
		//			return true;
		//		}
		return false;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<Point> pointTrimmer(double m, int n){
		//this is the meat of this class, it takes all the steps before to get here, but this method should compute angles between 
		// every point in the border ArrayList and remove those below a certain threshold (double m)

		// here is just some basic law cosine stuff, a,b,c are all angles, while A,B,C are their opposite sides
		//also, angle a is the angle that we want
		//		b|\
		//		 | \
		//		C|  \A
		//		 |   \
		//		 |    \
		//     a ------c
		//          B
		//i decided I need to get all the points out first, x1 and y1 are for the i-s point, x2 and y2 are for the i point, ect
		//also, the reason that they are all being cast as Points is because the way borderOrder is structured, border needs
		//to be a generic ArrayList and in retrospect doing it this was was kind of dumb, but it'll work a little faster

		//note that elements 0,1, n-1, and n are being handled separatly because the ArrayList doesn't roll over like I'd like it to

		//note this is in radians! if you get weird stuff it's probably that

		Point gp = new Point(-1,-1,-1); //i needed just a generic point object to get its class to compare later on
		for(int k = 0; k<border.size();k++){
			//System.out.println("inside the trimmer loop");
			if(border.get(k).getClass().equals((gp.getClass()))){
				border.remove(k);
				//System.out.println("durr?");
				k--;
			} else {
				for(int i=n; i<((ArrayList<ArrayList>) border.get(k)).size()-n;i++){
					//this merits explanation, the ArrayList border contains only ArrayLists at this point, the preceding if
					//statement, but the ArrayList itself is still only defined for generic objects, so within these
					//Parentheses, I'm casing border as an ArrayList of ArrayLists, getting the ArrayList I want, then getting
					//from that the element I want, then casting that as Point object, then getting the x/y values and saving it
					double x1 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i-n))).getx());
					double y1 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i-n))).gety());
					double x2 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i))).getx());
					double y2 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i))).gety());
					double x3 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i+n))).getx());
					double y3 = (((Point)((((ArrayList<ArrayList>)border).get(k)).get(i+n))).gety());
					double A = Math.sqrt( ((x1-x3)*(x1-x3)) + ((y1 - y3 )*(y1 - y3)) );
					double B = Math.sqrt( ((x2-x3)*(x2-x3)) + ((y2 - y3 )*(y2 - y3)) );
					double C = Math.sqrt( ((x1-x2)*(x1-x2)) + ((y1 - y2 )*(y1 - y2)));
					double a = Math.acos((((B*B)+(C*C))-(A*A))/(2*B*C));
					//System.out.println(a);
					boolean crosses = false;
					//ok, these if statements are pretty fun, essentially they check to see if the line that is being 
					//drawn to determine if the point should be removed is inside the shape or not, because if it is
					//you don't want to remove the element

					//EDIT: This code has been removed as it didn't work that well, instead I'm replacing it with code that will identify the value of the
					//pixel that is in the middle of the segment from x1,y1 to x3,y3, because if it's of a certain value we want to remove the point
					int midX = Math.round((float)((x1+x3)/2.0));
					int midY = Math.round((float)((y1+y3)/2.0));
					if(((Point) pic.get((midX )).get(midY)).getv() == threshold){
						crosses = true;
					}
					//						if(x2>x1 && y2<y1){
					//							//general idea here is x2 is greater than x1, but y2 is less than y1
					//							if(((Point) pic.get((Math.round((float)x2) - 1 )).get(Math.round((float)y2) + 1)).getv() == threshold){
					//								crosses = true;
					//							}
					//						}else if((x2 >x1 && y2>y1)){
					//							if(((Point) pic.get((Math.round((float)x2) - 1 )).get(Math.round((float)y2) - 1)).getv() == threshold){
					//								crosses = true;
					//							}
					//						}else if((x2<x1 && y2<y1)){
					//							if(((Point) pic.get((Math.round((float)x2) + 1 )).get(Math.round((float)y2) + 1)).getv() == threshold){
					//								crosses = true;
					//							}
					//						}else if((x2<x1 && y2>y1)){
					//							if(((Point) pic.get((Math.round((float)x2) + 1 )).get(Math.round((float)y2) - 1)).getv() == threshold){
					//								crosses = true;
					//							}
					//						}
					if(a<m && !crosses){
						remove.add(((Point)(((ArrayList)(border.get(k))).get(i))));
						//System.out.println("added a point to remove, " + i);
					}
				}
			}
		}

		return remove;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int borderMop(int d){
		//this is a simple method that cleans up the border ArrayList, as it gets lots of 1 or 2 length lists in it that
		//crash the trimmer, ergo, delete any lists that are of length 4 or less
		int numRemoved = 0;
		for (int i = 0 ; i<border.size();i++){
			if(border.get(i).getClass().equals(border.getClass())){
				//super simple stuff, if the list is of size 4 or less, it gets the axe
				if(((ArrayList<ArrayList>) border.get(i)).size() <=((2*d) + 2 )){
					border.remove(i);
					i--;
					numRemoved++;
				}
			}
		}
		return numRemoved;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Point> borderID(){
		//this here is a fun one, it goes through and with its helper identifies if a point exists on the edge of the area, i.e.
		//if it bordered by a point that is not of the correct value
		//one important thing to note is that this requires that the areaID method be called or else it will just die
		ArrayList temp = new ArrayList();
		border = temp; //wanted to reset the border as this method will be called more than once per run and I wanated it to recompute
		//which elements make up the border
		int length = area.size();
		for (int i= 0 ; i<length;i++){
			if(borderIDhelper(i)){ //this is what this entire method boils down to: call the helper, do that very many times
				border.add(area.get(i));
			}
			//if(Math.round((((double)i/(double)area.size())*100)) != Math.round((((double)(i-1.0)/(double)area.size())*100)) ){
			//System.out.println((int)(((double)i/(double)area.size())*100.) + "% done with border");
			//String tehe = "";
			//for(int q = 0; q<((double)i/(double)area.size())*100.; q++){
			//tehe += "|";
			//} //this was pretty fun to write, it's a rudimentary percent bar, every | represents 1% 
			//System.out.println(tehe);
			//}
		}
		return border;
	}
	private boolean borderIDhelper(int i){

		//this is a little helper to speed up the borderID method by making it skip checks
		//once a point has been identified as a border point
		//hah, the way it was configured when that was written the method added 18 hours of computer time, 18 hours!

		//this should be only ever called within the borderID, so it's private
		int misc = 0;
		int tY = area.get(i).gety();//for some reason the nesting here gave me trouble, so I threw my hands up in the air and
		//just decided to bring x and y out as ints
		int tX = area.get(i).getx(); 
		if(area.get(i).gety()<(height - 1)){
			if(((Point) pic.get(tX).get(tY +1)).getv() != ((Point) pic.get(tX).get(tY)).getv()){
				misc++;
			}
		}
		if (misc != 0){
			area.get(i).setEdge(1);
			//so this whole edge business is some pretty fun stuff, essentially what it does is it takes each pixel that is defined
			//as a border pixel and since we know what side is exposed it gets fed into these if statements, which are simple here
			//but there are 16 possible side states, which interestingly enough is the number or sides a pixel has, raised to the
			//number of states that each side can hold. anyway, this one is simple, but they get more intense
		}

		misc = 0;
		if(area.get(i).getx()<(width - 1)){
			if(((Point) pic.get(tX+1).get(tY)).getv() != ((Point) pic.get(tX).get(tY)).getv()){
				misc++;
			}
		}
		if (misc != 0){
			if(area.get(i).getEdge() == 1){
				area.get(i).setEdge(5);
			}else{
				area.get(i).setEdge(2);
			}
		}
		misc = 0;
		if(area.get(i).gety()>=(1)){
			if(((Point) pic.get(tX).get(tY -1)).getv() != ((Point) pic.get(tX).get(tY)).getv()){
				misc++;
			}
		}
		if (misc != 0){
			if(area.get(i).getEdge() == 1){
				area.get(i).setEdge(8);
			}else if(area.get(i).getEdge() == 5){
				area.get(i).setEdge(11);
			}else if(area.get(i).getEdge() == 2){
				area.get(i).setEdge(9);
			}else{
				area.get(i).setEdge(4);
			}
		}
		misc = 0;
		if(area.get(i).getx()>=1){
			if(((Point) pic.get(tX-1).get(tY)).getv() != ((Point) pic.get(tX).get(tY)).getv()){
				misc++;
			}
		}
		if (misc != 0){
			if(area.get(i).getEdge() == 1){
				area.get(i).setEdge(10);
			}else if(area.get(i).getEdge() == 8){
				area.get(i).setEdge(14);
			}else if(area.get(i).getEdge() == 5){
				area.get(i).setEdge(12);
			}else if(area.get(i).getEdge() == 11){
				area.get(i).setEdge(15);
			}else if(area.get(i).getEdge() == 2){
				area.get(i).setEdge(6);
			}else if(area.get(i).getEdge() == 9){
				area.get(i).setEdge(13);
			}else if(area.get(i).getEdge() == 4){
				area.get(i).setEdge(7);
			}else {
				area.get(i).setEdge(3);
			}
		}
		//see, told you it gets better, the highest one you'll see there is 15, but there is also a 0 state, as you can see below
		//and it is used to indicate that no sides are exposed to different colored pixels, i.e it isn't a border pieces, ergo
		// it returns false
		return(!(area.get(i).getEdge() == 0));
		//for a reference of what these numbers actually mean you'll have to talk to me, it's a pretty simple list but these sides
		//are very important later in the program, as they are very useful in determining what the next border pixel should be
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int borderOrder(){
		//this is a method that will (hopefully) order the border so that the trimmer can use it, the returned int is the number
		//of ArrayLists within the border ArrayList, this is if there are separate areas that are distinct from each other in the 
		//area ArrayList, it is important to note that to save time and space, if the int is 1, there will be 0 ArrayLists within 
		//border and border itself will be the ordered points
		//ArrayList borderC = new ArrayList();
		ArrayList borderF = new ArrayList();
		//int num15 = 0; //I was curious about how many 15s were being flagged and it seems like too many, so I added this
		// int numEdge = 0;
		int n =0;
		//This first loop essentially just takes out all of the edge #15s, which indicates that they share a border with only
		//non interior pixels, so they can't be used in any edge
		for (int j = 0; j<border.size(); j++){
			if(((Point)(border.get(j))).getEdge() == 15){
				//((Point)(border.get(j))).setChosen(true);
				borderF.add(border.get(j));
				border.remove(j);
				j -= 1;
				//num15++;
				//System.out.println("got a 15 " + num15 + " " + j);
			}
		}
		System.out.println("done with the type 15 points (points exposed to outside on all of their sides");
		//so I tried to arrange these in order of their likelihood of being the end element of an edge, so now we have pixels
		//that are bordred on only one side by pixels of the same value
		for (int j = 0; j<border.size(); j++){
			int c = 0;
			if((((Point)(border.get(j))).getEdge() == 14 || ((Point)(border.get(j))).getEdge() == 13 || ((Point)(border.get(j))).getEdge() == 12 
					|| ((Point)(border.get(j))).getEdge() == 11) && !(((Point)(border.get(j))).getChosen())){
				((Point)(border.get(j))).setChosen(true);
				ArrayList<Point> tempAL = new ArrayList<Point>();
				tempAL.add(((Point)(border.get(j))));
				//border.remove(j);
				n = j;

				while (c!=-1){
					if (c == -99){
						//System.out.println("calling EBN " + c + n);
						c = edgeBasedNext(n);
						//System.out.println("out of EBN" + c + n);
					}else{
						//System.out.println("calling NBI" + c + n);
						c = nextBorderis(n);
						//	System.out.println("out of NBI" + c + n);
					}
					if(c != -1 && c != -99){
						n = c;
						tempAL.add(((Point)(border.get(c))));
						((Point)(border.get(c))).setChosen(true);
					}
				}
				//System.out.println("got a border, length " + tempAL.size());
				borderF.add(tempAL);
			}
		}
		System.out.println("done with the type 11 - 14 points (those bounded on 3 side by exterior pixels)");
		for (int j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getEdge() == 10 || ((Point)(border.get(j))).getEdge() == 9 || ((Point)(border.get(j))).getEdge() == 8 
					|| ((Point)(border.get(j))).getEdge() == 7 || ((Point)(border.get(j))).getEdge() == 6 || ((Point)(border.get(j))).getEdge() == 5
					) && !(((Point)(border.get(j))).getChosen())){
				((Point)(border.get(j))).setChosen(true);
				ArrayList<Point> tempAL = new ArrayList<Point>();
				tempAL.add(((Point)(border.get(j))));
				//border.remove(j);
				int c = 0;
				n = j;

				while (c!=-1){
					if (c == -99){
						//System.out.println("calling EBN " + c + n);
						c = edgeBasedNext(n);
						//System.out.println("out of EBN" + c + n);
					}else{
						//System.out.println("calling NBI" + c + n);
						c = nextBorderis(n);
						//	System.out.println("out of NBI" + c + n);
					}
					if(c != -1 && c != -99){
						n = c;
						tempAL.add(((Point)(border.get(c))));
						((Point)(border.get(c))).setChosen(true);
					}
				}
				//System.out.println("got a border, length " + tempAL.size());
				borderF.add(tempAL);
			}
		}
		System.out.println("done with the type 5-10 points (those bounded on 2 sides by exterior pixels)");
		for (int j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getEdge() == 4 || ((Point)(border.get(j))).getEdge() == 3 || ((Point)(border.get(j))).getEdge() == 2 
					|| ((Point)(border.get(j))).getEdge() == 1) && !(((Point)(border.get(j))).getChosen())){
				((Point)(border.get(j))).setChosen(true);
				ArrayList<Point> tempAL = new ArrayList<Point>();
				tempAL.add(((Point)(border.get(j))));
				//border.remove(j);
				int c = 0;
				n = j;

				while (c!=-1){
					if (c == -99){
						//System.out.println("calling EBN " + c + n);
						c = edgeBasedNext(n);
						//System.out.println("out of EBN" + c + n);
					}else{
						//System.out.println("calling NBI" + c + n);
						c = nextBorderis(n);
						//	System.out.println("out of NBI" + c + n);
					}
					if(c != -1 && c != -99){
						n = c;
						tempAL.add(((Point)(border.get(c))));
						((Point)(border.get(c))).setChosen(true);
					}
				}
				//System.out.println("got a border, length " + tempAL.size());
				borderF.add(tempAL);

			}
		}
		border = borderF;
		return borderF.size();
	}
	public int edgeBasedNext(int i){
		//this is what became of the max length method I had planned, the idea is that the best next point will always be related to
		//the side that is the border with the outside, essentially every one of the 15 possible edge states has its own set of
		//optimal edge spaces, so this method will take in a point, get which side contacts the outside and will then scan the 
		//border ArrayList to see which of the remaining points will fit best as the next edge pixel
		//it is important to note that this method will allow for the reuse of points as it does not screen for the chosen bit,
		//however it will take them into consideration in certain cases 

		//EDIT: the 14th: i added the chosen? boolean to the ifs, so it won't select already selected points, but that means it
		//loses some of its flexibility, but it will no longer get stuck in infinite loops all the time

		//EDIT: 15th: there is a serious problem with the chosen variable, I need it to stop infinite loops, but for some reason it
		//always is true, i've looked everywhere in every file and I can't find where it's getting a true value

		//EDIT: Later on the 15th: no duh the chosen variable was giving you trouble, it was getting the chosen state for i, not
		//j, which is why I never should have passed in a variable named i, I'm sorry, it's confusing
		int edge = ((Point)(border.get(i))).getEdge();
		//		System.out.println("in EBN, "  + edge + " " + ((Point)(border.get(i))).getx() + " " + ((Point)(border.get(i))).gety());
		//		System.out.println("current border length is " + border.size());
		//		Keyboard.readString();
		if (edge == 14){
			for ( int j = 0; j<border.size(); j++){
				//				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
				//							== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
				//					return j;
				//				}
				//				if((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() )){
				//					System.out.println(((Point)(border.get(j))).getx() + " " + ((Point)(border.get(j))).gety() + " " + 
				//							((Point)(border.get(j))).getChosen());
				//				}
				//				if(((((Point)(border.get(j))).gety()-1) == ((Point)(border.get(i))).gety())){
				//					System.out.println(((Point)(border.get(j))).getx() + " " + ((Point)(border.get(j))).gety() + " " +
				//							((Point)(border.get(j))).getChosen());
				//				}
				//System.out.println(((Point)(border.get(j))).getx() + " " + ((Point)(border.get(j))).gety());
			}
			//Keyboard.readString();
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
				//System.out.println(((Point)(border.get(j))).getx() + " " + ((Point)(border.get(j))).gety());
			}
			//Keyboard.readString();
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
				//System.out.println(((Point)(border.get(j))).getx() + " " + ((Point)(border.get(j))).gety());
			}
			//Keyboard.readString();
			//System.out.println("EBN fail type 14");
			return -1;
		}else if (edge == 13){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety() -1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 13");
			return -1;
		}else if (edge == 12){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 12");
			return -1;
		}else if (edge == 11){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 11");
			return -1;
		}else if (edge == 10){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 10");
			return -1;
		}else if (edge == 9){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()   == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 9");
			return -1;
		}else if (edge == 8){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 8");
			return -1;
		}else if (edge == 7){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 7");
			return -1;
		}else if (edge == 6){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()   == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 5");
			return -1;
		}else if (edge == 5){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 6");
			return -1;
		}else  if (edge == 4){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 4");
			return -1;
		}else if (edge == 3){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 3");
			return -1;
		}else if (edge == 2){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 2");
			return -1;
		}else if (edge == 1){
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() -1  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			for ( int j = 0; j<border.size(); j++){
				if(((((Point)(border.get(j))).getx()  == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
						== ((Point)(border.get(i))).gety())) && !((Point)(border.get(j))).getChosen()){
					return j;
				}
			}
			//System.out.println("EBN fail type 1");
			return -1;
		}else{ System.out.println("EBN fail type 0");return -1;}
		//ugh, i just realized that borders are no good unless they're around something, i e not just a line
	}
	public int nextBorderis(int i){
		// well this is going to be a pretty long method, essentially what it's doing is it's doing is it's taking i from borderOrder
		//then it's checking the 8 spaces around it for possible next border pixels, there are a few return codes, for instance,
		//if two different pixels that can be used as next edge pieces are identified, -99 will be returned, if none are found, 
		//-1 will be returned (I would use 0, but there is a 0th element), and in all other cases, the index of the element that will
		//make the proper next border will be returned

		//it is important to note that nextBorderis and edgeBasedNext both do the same type of thing, but nextBorderis is much faster 
		//and more efficient but tends to throw up more situations it can't handle. for this reason nextBorderis is essentially the
		//default while edgeBasedNext is called when there are issues that nextBorderis can't handle
		ArrayList<Integer> next = new ArrayList<Integer>();
		int misc = 0;
		int j = 0;
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
					== ((Point)(border.get(i))).gety())&& !((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
					== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
					== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		if(((Point)(border.get(i))).getx()>=(1)){
			for ( j = 0; j<border.size(); j++){
				if((((Point)(border.get(j))).getx() +1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
						== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
					misc++;
					next.add(j);
				}
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
					== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()-1) 
					== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()) 
					== ((Point)(border.get(i))).gety())&&!((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		for ( j = 0; j<border.size(); j++){
			if((((Point)(border.get(j))).getx() -1 == ((Point)(border.get(i))).getx() ) && ((((Point)(border.get(j))).gety()+1) 
					== ((Point)(border.get(i))).gety()) && !((Point)(border.get(j))).getChosen()){
				misc++;
				next.add(j);
			}
		}
		if (misc == 1){
			return ((int)(next.get(0)));
		}else if(misc>1){
			return -99;
		}
		return -1;
	}
	public ArrayList<Point> AreaExclude(int value){
		ArrayList<Point> temp = new ArrayList<Point>();
		area = temp;
		threshold = value;
		for (int i = 0; i<width;i++){
			for (int j = 0; j<height; j++){
				if (((Point)(pic.get(i).get(j))).getv() != value){
					area.add(((Point)(pic.get(i).get(j))));
					//again, this looks ugly but is quite simple, the method is getting the
					//point from the pic arraylist and the sublist in that, then if it
					//is above the given value, it is added to the area ArrayList
					//this is repeated for all points
					//System.out.println("added a point! " + i + " , " + j);
				}
			}
			//System.out.println((int)(((double)i/(double)width)*100.) + " % done");
			//It is important to note that these points are in no order other than from bottom
			//to top by column, this is why the borderID is so important
		}
		return area;
	}
	public ArrayList<Point> AreaID(int value){
		ArrayList<Point> temp = new ArrayList<Point>();
		area = temp;
		threshold = value;
		for (int i = 0; i<width;i++){
			for (int j = 0; j<height; j++){
				if (((Point)(pic.get(i).get(j))).getv() >= value){
					area.add(((Point)(pic.get(i).get(j))));
					//again, this looks ugly but is quite simple, the method is getting the
					//point from the pic arraylist and the sublist in that, then if it
					//is above the given value, it is added to the area ArrayList
					//this is repeated for all points
					//System.out.println("added a point! " + i + " , " + j);
				}
			}
			//System.out.println((int)(((double)i/(double)width)*100.) + " % done");
			//It is important to note that these points are in no order other than from bottom
			//to top by column, this is why the borderID is so important
		}
		return area;
	}
	public int fillAreaOld(int threshold, String name, String writeName){
		remove = new ArrayList<Point>();
		for(int i = 1 ; i < pic.size()-2; i++){
			for(int k = 1; k <pic.get(0).size()-2; k++){
				if(pic.get(i).get(k).getv() != -1){
					if(pic.get(i-1).get(k).getv() == -1 &&pic.get(i+1).get(k).getv() == -1 &&pic.get(i).get(k-1).getv() == -1 ||
							pic.get(i-1).get(k).getv() == -1 &&pic.get(i+1).get(k).getv() == -1 &&pic.get(i).get(k+1).getv() == -1 ||
							pic.get(i-1).get(k).getv() == -1 &&pic.get(i).get(k+1).getv() == -1 &&pic.get(i).get(k-1).getv() == -1 ||
							pic.get(i+1).get(k).getv() == -1 &&pic.get(i).get(k-1).getv() == -1 &&pic.get(i).get(k+1).getv() == -1){
						remove.add(pic.get(i).get(k));
					}
				}
			}
		}
		writepixels(-1, name, writeName);
		return remove.size();
	}
	public void fillArea(int value, int value2, String name, String writeName){
		//pic = new ArrayList<ArrayList<Point>>();
		//convertJPG(name);
		//ArrayList<Point> removeTemp = new ArrayList<Point>();
		remove = new ArrayList<Point>();
		boolean inside= false;
		for(int i = 1 ; i < pic.size()-2; i++){
			for(int k = 1; k <pic.get(0).size()-2; k++){
				//System.out.println("inside");

				if(pic.get(i).get(k).getv() == -1 && pic.get(i).get(k+1).getv() != -1){
					inside = (!(inside));
					System.out.println("inside 2 " + inside + " " + k);
				}
				if(inside){
					//System.out.println("inside 1");
					remove.add(pic.get(i).get(k));
					//System.out.println("inside 1");
				}
			}
			System.out.println(remove.size());
			System.out.println("outside 2");
			inside = false;
		}
		System.out.println("Filled " + remove.size() + " pixels");
		writepixels(-1, name, writeName);
	}
	public void fillArea(int value, int value2, String name, String writeName, ArrayList<Point> rings){
		//pic = new ArrayList<ArrayList<Point>>();
		//convertJPG(name);
		boolean tempB = true;
		//ArrayList<Point> removeTemp = new ArrayList<Point>();
		remove = new ArrayList<Point>();
		boolean inside= false;
		for(int i = 1 ; i < pic.size()-2; i++){
			for(int k = 1; k <pic.get(0).size()-2; k++){
				//System.out.println("inside");
				for(int j = 0 ; j < rings.size(); j++){
					if(i == rings.get(j).getx() && k == rings.get(j).gety()){
						for(int l = 0 ; l < rings.size(); l ++){
							if(i == rings.get(j).getx() && k+1 == rings.get(j).gety()){
								tempB = false;
							}
						}
						if(tempB){
							inside = (!(inside));
							System.out.println("inside 2 " + inside + " " + k);
						}
						
					}
				}
				tempB = true;
//				if(pic.get(i).get(k).getv() == -1 && pic.get(i).get(k+1).getv() != -1){
//					inside = (!(inside));
//					System.out.println("inside 2 " + inside + " " + k);
//				}
				if(inside){
					remove.add(pic.get(i).get(k));
					//System.out.println("inside 1");
				}
			}
			System.out.println(remove.size());
			System.out.println("outside 2");
			inside = false;
		}
		System.out.println("Filled " + remove.size() + " pixels");
		writepixels(-1, name, writeName);
	}
}
//This method is designed to resolve issues that arise when the borderOrder (actually nextBorderis) encounters a situation
//where there are two different paths that it could take, the goal of this method is to maximize the distance that a single
//edge covers, without overlap. just because of what it is doing, this will be slow, as it will draw a path, check and make
//sure that every single line that was drawn does not overlap any other line, and then repeat that for every path, picking
// the longest path possible - Note, the distance will be measured in segments, NOT pixels, there is no real difference, but 
// using segments allows for the use of an int in the length
