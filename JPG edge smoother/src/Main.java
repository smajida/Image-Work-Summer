import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import cs1.Keyboard;
//import cs1.*;
public class Main {
	public static void main (String[] args){
		System.out.println("Info: Starting Java based image analysis program");
		long start = System.currentTimeMillis();
		//this is just the runner for the program, it interacts with the user, essentially it creates the object brain from
		//user inputs, then just runs all the methods for it, it's just a shell to interact and call methods
		//also, there are System.out.println's everywhere for just displaying how the program is doing, it should show if the
		// program crashes, and in the Future I'd like to add progress terms, to show that it is in fact running because just the
		// way this program is structured and the way java works, it's going to be VERY slow
		String picName;
		int areaValue = 0; int convertValue =0; int tempI = 0;int digits = 0; int pS = 0;  int d = 0; int x = 0;int y = 0; int distance = 0; boolean debug = false;
		double measure = 0; boolean constant = false;int database = 1; String  suffix = "unspecified"; double step = 0;boolean checkColor = false; long lastTime;
		double expectedCompletion= 0; double tempD=-99; double resolution = .1; boolean oldCode = false; boolean ring = false; boolean writeDifferent = false;
		String writePath;
		//So here is some fun stuff, reading in a text document at some one of the listed options, the document it reads in contains the settings
		FileRead fr = new FileRead();
		ArrayList<String> locations = new ArrayList<String>();
		//possible settings save locations going here, might use this outside of just testing
		try{
			if(args[0] != null){
				locations.add(args[0]);	
			}
			System.out.println("Info: following path will be used for preference file location: " + locations.get(0));
		} catch (ArrayIndexOutOfBoundsException e){
			locations = generatePaths();
		}
		//fr.writeFile(locations, path);
		//ArrayList<String> readOutput = new ArrayList<String>();
		locations = fr.readFile(locations);
		//System.out.println(locations.size());
		if(locations.size() == 0){
			try{
				if(args[0] != null){
					locations.add(args[0]);	
				}
				//System.out.println("Info: following path will be used for preference file location: " + locations.get(0));
			} catch (ArrayIndexOutOfBoundsException e){
				locations = generatePaths();
			}
			ArrayList<String> settingsAL = generateSettingsAL();
			String path = fr.writeFile(locations,  settingsAL);
			if(path.substring(0,2).equals("-99")){
				locations.add(path.substring(3));
				locations = fr.readFile(locations);
			}else if(path.substring(0,2).equals("-98")){
				System.out.println("the next time this program starts, you will enter the same menu as before, please re enter your file path there");
				return;
			}else{return;}
		}
		System.out.println("Info: Preferences configuration file read");
		picName = locations.get(3).substring(12, locations.get(3).length()-1);
		suffix = locations.get(6).substring(10, locations.get(6).length()-1);
		writePath = locations.get(33).substring(13, locations.get(33).length()-1);
		if(locations.get(2).substring(11).equalsIgnoreCase("true")){
			constant = (locations.get(4).substring(11).equals("true"));
			if(constant){
				char c = locations.get(5).charAt(9);
				if(c-48>0 && c-48<10){d = c-48;}else {d = 4;}
			}
		}
		if(locations.get(16).substring(7).equalsIgnoreCase("true")){ring = true;}
		if(locations.get(22).substring(10).equalsIgnoreCase("true")){checkColor = true;}
		if(locations.get(23).substring(10).equalsIgnoreCase("true")){debug = true;}
		if(locations.get(24).substring(10).equalsIgnoreCase("true")){oldCode = true;}
		if(locations.get(32).substring(17).equalsIgnoreCase("true")){writeDifferent = true;}

		char ch = locations.get(28).charAt(7);
		if(ch-48>0 && ch-48<10){pS = ch-48;}else {d = 4;}
		//these for loops all look kinda the same, mostly because they are, but they're taking the character values that the 
		//program is getting from the text file and converting them to integer values
		for(int i = 0 ; i< locations.get(7).length()-12; i++){
			char c = locations.get(7).charAt(locations.get(7).length()-i-1);
			if(c == 45){
				i = 999999;
				areaValue = areaValue * -1;
			} else if( c == 32){
				i = 999999;
			}else{areaValue += ((c-48)*(Math.pow(10, i)));}
		}
		//System.out.println(areaValue);
		//Keyboard.readChar();
		for(int i = 0 ; i< locations.get(8).length()-15; i++){
			char c = locations.get(8).charAt(locations.get(8).length()-i-1);
			if(c == 45){
				i = 999999;
				convertValue = convertValue * -1;
			} else if( c == 32){
				i = 999999;
			}else{convertValue += ((c-48)*(Math.pow(10, i)));}
		}
		for(int i = 0 ; i< locations.get(8).length()-15; i++){
			char c = locations.get(8).charAt(locations.get(8).length()-i-1);
			if(c == 45){
				i = 999999;
				convertValue = convertValue * -1;
			} else if( c == 32){
				i = 999999;
			}else{convertValue += ((c-48)*(Math.pow(10, i)));}
		}
		for(int i = 0 ; i< locations.get(10).length()-12; i++){
			char c = locations.get(10).charAt(locations.get(10).length()-i-1);
			if(c == 45){
				i = 999999;
				distance = distance * -1;
			} else if( c == 32){
				i = 999999;
			}else{distance += ((c-48)*(Math.pow(10, i)));}
		}
		for(int i = 0 ; i< locations.get(18).length()-8; i++){
			char c = locations.get(18).charAt(locations.get(18).length()-i-1);
			if(c == 45){
				i = 999999;
				x = x * -1;
			} else if( c == 32){
				i = 999999;
			}else{x += ((c-48)*(Math.pow(10, i)));}
		}
		for(int i = 0 ; i< locations.get(19).length()-8; i++){
			char c = locations.get(19).charAt(locations.get(19).length()-i-1);
			if(c == 45){
				i = 999999;
				y = y * -1;
			} else if( c == 32){
				i = 999999;
			}else{y += ((c-48)*(Math.pow(10, i)));}
		}
		//System.out.println("out of integers");
		int DecimalDigits = 0;
		for(int i = 0 ; i < locations.get(11).length()-15; i ++){
			char c = locations.get(11).charAt(locations.get(11).length()-i-1);
			if(c == 45){
				i = 999999;
				tempI = tempI * -1;
			} else if( c == 32){
				i = 999999;
			}else if(c == 46){
				//System.out.println(locations.get(11).length()-(i+2));
				measure =  locations.get(11).charAt(locations.get(11).length()-(i+2)) - 48;
				measure += tempI*(Math.pow(10,-DecimalDigits));
				i = 999999;
			}else{
				DecimalDigits++;
				tempI += ((c-48)*(Math.pow(10, i)));
			}
		}
		DecimalDigits = 0;
		tempI = 0;
		for(int i = 0 ; i < locations.get(13).length()-7; i ++){
			char c = locations.get(13).charAt(locations.get(13).length()-(i+1));
			if(c == 45){
				i = 999999;
				tempI = tempI * -1;
			} else if( c == 32){
				i = 999999;
			}else if(c == 46){

				step =  locations.get(13).charAt(locations.get(13).length()-(i+2)) - 48;
				step += tempI*(Math.pow(10,-DecimalDigits));
				i = 999999;
			}else{
				DecimalDigits++;
				tempI += ((c-48)*(Math.pow(10, i)));
			}
		}
		DecimalDigits = 0;
		tempI = 0;
		for(int i = 0 ; i < locations.get(15).length()-14; i ++){
			char c = locations.get(15).charAt(locations.get(15).length()-(i+1));
			if(c == 45){
				i = 999999;
				tempI = tempI * -1;
			} else if( c == 32){
				i = 999999;
			}else if(c == 46){

				resolution =  locations.get(15).charAt(locations.get(15).length()-(i+2)) - 48;
				resolution += tempI*(Math.pow(10,-DecimalDigits));
				i = 999999;
			}else{
				DecimalDigits++;
				tempI += ((c-48)*(Math.pow(10, i)));
			}
		}
		if(debug){
			System.out.println(picName);
			System.out.println(suffix);
			System.out.println(constant);
			System.out.println(digits);
			System.out.println(areaValue);
			System.out.println(convertValue);
			System.out.println(distance);
			System.out.println(measure);
			System.out.println(step);
			System.out.println(x);
			System.out.println(y);
			System.out.println(pS);
			System.out.println(resolution);
			System.out.println(ring);
			System.out.println(writeDifferent);
			System.out.println(writePath);
		}
		//Keyboard.readInt();
		String root = picName;
		String wroot = writePath;
		@SuppressWarnings({ "rawtypes",/* "unused"*/ })
		ArrayList temp;
		int loopvar = 0;
		//need to fix so next Name works
		if(checkColor && database!=0){
			Compute brain = new Compute();
			brain.convertJPG( nextName(root, constant, 0, d, suffix));
			System.out.println("The auto detected size is: " + brain.width + " by " +  brain.height);
			areaValue = brain.pixelGetFD(nextName(root, constant, 0, d, suffix), 2);
		} 
		database = dbID(picName,constant,d,suffix);
		ArrayList<Point> specialTrimmerList;
		//@SuppressWarnings("unused")
		boolean tempB = true;
		while(loopvar<database && !oldCode){
			//while(loopvar<4){
			lastTime = System.currentTimeMillis();
			if(database !=1){
				if(writeDifferent){writePath = nextName(wroot, constant, loopvar, d, suffix);}else{writePath =  nextName(root, constant, loopvar, d, suffix);}
				picName = nextName(root, constant, loopvar, d, suffix);
				System.out.println("Info: Computing image " + (loopvar+1) + " with " + (database - (loopvar+1)) + " image(s) remaining");
			}
			Compute brain = new Compute();
			brain.convertJPG(picName);
			if(pS == 1){
				do{
					temp = brain.AreaID(areaValue);
					temp = brain.borderID();
					brain.borderOrder();
					tempB = brain.knitter();
					temp = brain.border;
					tempI = brain.borderMop(distance);
					specialTrimmerList = brain.pointTrimmer(measure, distance);
					tempB = brain.writepixels(convertValue, picName,writePath);
				}while(specialTrimmerList.size()>0);
			}else if(pS == 2){
				brain.AreaID(areaValue);
				ArrayList<LPoint> ellipse = brain.ellipseFit(step);
				System.out.println(ellipse.size());
				brain.setRemove(ellipse);
				brain.writepixels(-65536, picName, writePath);
			}else if (pS == 3){
				ArrayList<Point> ringStore = new ArrayList<Point>();
				brain.AreaID(areaValue);
				ArrayList <LPoint> shape = brain.LsetO(areaValue, picName, debug);
				shape = brain.Lset(areaValue, picName, shape, debug, resolution, writePath);
				brain.setRemove(shape);
				for(int i = 0; i < shape .size(); i ++){
					ringStore.add(shape.get(i));
				}
				brain.writepixels(-1, picName, writePath);
				if(ring){
					tempI = areaValue;
					areaValue = brain.pic.get(0).get(0).getv();
					shape = brain.LsetO(areaValue, picName, shape, writePath, debug);
					shape = brain.Lset(areaValue, picName, shape, debug, resolution, writePath);
					brain.setRemove(shape);
					for(int i = 0; i < shape .size(); i ++){
						ringStore.add(shape.get(i));
					}
					System.out.println(shape.size() + " " + ringStore.size());
					brain.writepixels(-1, picName, writePath);
					brain.fillArea(-1, tempI, picName, writePath, ringStore);
					//if(debug){System.out.println("The missed pixel filler added " + tempI + " pixels");}
					areaValue = tempI;
				}
			}else if (pS == 4){
				brain.AreaID(areaValue);
				ArrayList <Point> shape = brain.expandToFill(x,y);
				brain.PsetRemove(shape);
				brain.writepixels(-1, picName, writePath);
			}else{
				System.out.println("well this is a little odd, you're not in any of the programs, please chech your settings text file");	
			}
			loopvar++;
			expectedCompletion = ((loopvar)*tempD + (((double) System.currentTimeMillis() - lastTime) / 1000))/ (loopvar+1);
			expectedCompletion =  (database-(loopvar+1)) * expectedCompletion;
			tempD = expectedCompletion/(database-(loopvar+1));
			System.out.println("Info: The last image took " + ((double)System.currentTimeMillis() - lastTime)/1000 +
					" seconds\nInfo: The estimated time remaining is " + expectedCompletion + " seconds");
		}
		if(debug){
			System.out.println("Info: Running this program took " + ( (double)System.currentTimeMillis() - (double)start)/1000.0 + " seconds");
		}

		//}


		// so essentially this entire section is just old code, it's not used, I might *might* add a way to access it, but I don't really see a need, most of the time
		//I would just force the inputs anyway, so whatever, it's dead for now and I should probably move it, but first I need to decide its fate

		if(oldCode){
			//the ASCII value for '-' is 45, and the ASCII value for ' ' is 32, code for '.' is 46


			//@SuppressWarnings("rawtypes")
			//ArrayList temp = new ArrayList(); boolean tempB;
			ArrayList<Integer> areaValues = new ArrayList<Integer>();
			//Keyboard k = new Keyboard(); 
			System.out.println("This text based runner is only a version, gui to be added later, \nenter 1 for the edge smoother(doesn't work too hot)" +
					"  \nenter 2 for the ellipse finder(experimental)\nenter 3 for level set(the only thing that really works right now)\nenter 4 for area filling code" +
					"(experimental)");
			//pS = readInt();
			pS = 3;
			if(pS ==1){
				System.out.println("Welcome to the simple edge finder and smoother tool, there are a few things you need:");
			}else if (pS == 2){
				System.out.println("Welcome to the ellipse fitting tool, before we continue, here is a description of what this tool does: \nThis tool creates a circle" +
						"or ellipse that is exactly the size of the image, from there it will \"fall\" inwards, minimizing its energy, which is a function of its fit to" +
						"elements in the image. In short, it seeks to fit a shape to the outermost edge of the selected area");
			} else if (pS == 3){
				System.out.println(" Welcome to the level set tool, this is similar to the ellupse fitting tool (they're actually in the same class if you're looking at" +
						" the source code) but this one uses individual pixels that it calculates forces on then adds those forces to the points X and Y values" +
						" (as another side note, those points in the edge have a special data type, an extension of the Point calss called LPoint)");
			}
			System.out.println("You need the image you want computed \n you need the value of the pixels that are to be the area" +
					" you want analyzed \n and you need to know what the minimum angle between points should be \n This is at this" +
					" time not a completed program,\n and you can contact Sam Weiss with questions (samuelweiss@earthlink.net)" +
					"\n\n\n please be warned: this program will take a long time for larger images, it is a slow program\n" +
					" written in a slow language, with poor optimization");
			System.out.println("\n Will you be using this program to analyze a database?\nif so, enter '1', and if not, enter '2'");
			//tempI = readInt();
			tempI = 1;
			if(tempI == 1){
				System.out.println("ok, this requires a little more information then:\nsoon the program is going to ask you for the name" +
						" of the picture \nit is very important that you only enter the ROOT name\n an example of this would be in the " +
						"set of images \n {human001.png, human002.png, human003.png}\nyou would enter '/home/samweiss/human' as that is the name, less numbers" +
						"\nyou will also be asked for the digit count, in the above list the digit count is CONSTANT\n(all names have the same number" +
						" of digits after it)\n and the digit count itself is 3 digits\n (00X, 3 digits, simple)\nyou will also be asked for the number" +
						"of pictures in the set, which, for the set above, would be 3\nit is somewhat important to note that this" +
						" program starts at 0, not 1, most databases start there");
				//ok, using try and catch I should be able to make it auto detect how many photos there are, but later

			}
			System.out.println("\n\n\n Now we will start the entry phase \n Please enter the name of the image you want computed" +
					"\n INCLUDE FILE SUFFIX for individual photos, not databases! Include file path for ALL" +
					"\n (*.jpg, *.TIFF, *.PNG) and the file path (c:/...(windows)  /home/...(Ubuntu) /Users/...(OSX) ect)");
			//need to find which formats it can accept
			//picName = readString();
			picName = "/users/samuelweiss/ratDB copy/C005-mouse2-filtered4-labels.";
			if(tempI == 1){
				System.out.println("ok, so as mentioned above, please now enter if the number of digits is CONSTANT (true/false)");
				//constant = readBoolean();
				constant = true;
				if (constant){
					System.out.println("ok, now please enter the number of digits that follow ");
					//digits = readInt();
					digits = 4;
				}
				System.out.println("now please enter the file suffix of the images (.png, .jpg, etc) [include the period!]");
				//suffix = readString();
				suffix = ".png";
				//public static
				d = digits;
				database = dbID(picName,constant,d,suffix);
			}else{ d = 0;}
			//this section of x and y inputs is now commented because there is now an auto detecting section of the converting method
			//System.out.println("Now please enter the number of pixels on the bottom");
			//xLength = k.readInt();
			//System.out.println("Now please enter the number of pixels on the side");
			//yLength = k.readInt();
			System.out.println("Now please enter the CONVERTED HEX RGB VALUES \n if you don't know what this is, enter 1 \n " +
					" alternatively, if you want to run a wizard that will identify\n all the pixel values and return the values that " +
					" make up a specified amount of the screen, enter 2");
			//areaValue = k.readInt();
			areaValue = -16711423;
			if (areaValue == 1){
				System.out.println("So essentially the way that java, and therefore this program, handles pixel color is in a single" +
						" integer value, which is a converted hex value. Normal RGB values would be in 3 different hex values, each" +
						" of which range from <00> to <ff>, but java doesn't like non base 10 stuff, so it converts it to base 10" +
						" and removes the spaces between the digits, so we go from something like <00 ff 00> to " + Math.pow(256, 2) +
						" essentially how you convert is you take the hex value, convert it to base 10, then multiply it by 256 for" +
						" green, and 256^2 for red (multiply by 1 for blue)\n\n\n EDIT: all that stuff you just read, yeah it" +
						" doesn't actually give out values like that, they're all negative, and I just don't know entirely what" +
						" to do to get them, so here's a little helper I wrote");
				areaValue = 2;
			}  if(areaValue == 2){
				System.out.println("\nconsider yourself warned, this is VERY slow, as in slower than the rest of the program, which is slow" +
						"\n\n if you still want to proceede, enter a little less than the % of the picture you think is made up of pixels" +
						" you want, or -99 if you want to quit, I do not suggest using this for images more than 1000 by 1000, as it " +
						" can take over an hour because man does it slow down at the end \n I reccomend starting this and then like" +
						" kicking back for a long lunch, or starting it before you go to bed because it just takes forever, I mean I" +
						" commented like 60% of this program while I was waiting for it, and I was doing a big photo on a bad computer" +
						" but it took a super long time /n /n so a while after I wrote that I kinda fixed the method and now it actually" +
						" works at an acceptable speed as it tests only 10000 pixels as opposed to the entire image");
				int pc = 2;//k.readInt();
				root = picName;
				if(pc == -99){
					return;
				}else{
					Compute brain = new Compute();
					brain.convertJPG( nextName(root, constant, 0, d, suffix));
					System.out.println("The auto detected size is: " + brain.width + " by " +  brain.height);
					temp = brain.pixelGetF(nextName(root, constant, 0, d, suffix), pc);
					System.out.println(temp.toString());
					System.out.println("please now chose one of these values by entering it");
					areaValue = Keyboard.readInt();
					areaValues.add(areaValue);
				}
			}
			System.out.println("on a similar note, please enter the value that you want the points that are rejected to have");
			//convertValue = k.readInt();
			convertValue = -1;
			if(pS == 1){
				System.out.println("now please enter the pixel test distance you want: this is the number of pixels on either side that the \n trimmer uses to determine" +
						" the angle of the point being examined");
				int checkDist = Keyboard.readInt();
				System.out.println("Now please enter the minimum angle that is allowed in radians");
				measure = Keyboard.readDouble();
				loopvar = 0;
				root = picName;
				while(loopvar<database){
					if(database !=1){
						picName = nextName(root, constant, loopvar, d, suffix);
					}
					Compute brain;
					//ArrayList<Point> specialTrimmerList;
					int wut;
					do{
						brain = new Compute();
						tempB = brain.convertJPG(picName);
						if(tempB){
							System.out.println("There was an error loading your image, sorry, this program will now quit, you can restart to try again");
							return;
						} else {System.out.println("image read successfully");}
						tempB = false;
						while (!tempB){
							ArrayList<Integer> pixelList = brain.pixelGet(picName, 0);
							for (int i= 0;i<pixelList.size();i++){
								for(int j = 0;j<areaValues.size();j++){
									//System.out.println(areaValues.get(j));
									if(pixelList.get(i) == (int)areaValues.get(j)){
										tempB = true;
										areaValue  = areaValues.get(j);
										j = areaValues.size();
										i = pixelList.size();
										tempB= true;
									}
								}
							}
							if(!tempB){
								System.out.println("There was an image on which the specified color was not found, the colors found were:\n" + 
										pixelList.toString() + "\nplease choose one");
								areaValue = Keyboard.readInt();
								areaValues.add(areaValue);
							}
						}

						temp = brain.AreaID(areaValue);
						System.out.println("The following has been identified as inside the area " + temp.size());
						temp = brain.borderID();
						System.out.println("The following has been identified as the border " + temp.size());
						brain.borderOrder();
						tempB = brain.knitter();
						//if(tempB){
						//System.out.println("the knitter flagged/knit at least one set of points, yay!");
						//}
						temp = brain.border;
						System.out.println("There were " + temp.size() + " points identified as border points");
						int numRemoved = brain.borderMop(checkDist);
						System.out.println("There were " + numRemoved + " elements that were of length less than " + ((2*checkDist)+2) + " removed by the mopper");
						specialTrimmerList = brain.pointTrimmer(measure, checkDist);
						System.out.println("There were " + specialTrimmerList.size() + " points flagged for removal");
						wut = specialTrimmerList.size();
						tempB = brain.writepixels(convertValue, picName);
						//System.out.println(specialTrimmerList.size());
						if(tempB){
							System.out.println("There was an error loading your image, sorry, this program will now quit, you can restart to try again");
							return;
						} else {System.out.println("write done");}
						//System.out.println(specialTrimmerList.size() + " " + (Integer)wut);
					}while (wut >0);
					loopvar++;
				}
				System.out.println("Well it looks like we're done here, the photo(s) should be smoothed");
			} else if (pS == 2){
				System.out.println("please enter the step that you want the ellipse tool to use with each iterative step (think small numbers, like .01)");
				step = Keyboard.readDouble(); loopvar = 0;
				root = picName;
				while(loopvar<database){
					if(database !=1){
						picName = nextName(root, constant, loopvar, d, suffix);
					}
					Compute brain = new Compute();
					tempB = false;
					while (!tempB){
						brain = new Compute();
						ArrayList<Integer> pixelList = brain.pixelGet(picName, 0);
						for (int i= 0;i<pixelList.size();i++){
							for(int j = 0;j<areaValues.size();j++){
								//System.out.println(areaValues.get(j));
								if(pixelList.get(i) == (int)areaValues.get(j)){
									tempB = true;
									areaValue  = areaValues.get(j);
									j = areaValues.size();
									i = pixelList.size();
									tempB= true;
								}
							}
						}
						//					if(!tempB){
						//						System.out.println("There was an image on which the specified color was not found, the colors found were:\n" + 
						//								pixelList.toString() + "\nplease choose one");
						//						areaValue = k.readInt();
						//						areaValues.add(areaValue);
						//					}
					}
					loopvar++;
					brain.AreaID(areaValue);
					ArrayList<LPoint> ellipse = brain.ellipseFit(step);
					System.out.println(ellipse.size());
					brain.setRemove(ellipse);
					brain.writepixels(-65536, picName);
				}
			} else if (pS == 3){
				loopvar = 0;
				Compute brain = new Compute();
				root = picName;
				areaValues.add(areaValue);
				while(loopvar<database){
					if(database !=1){
						picName = nextName(root, constant, loopvar, d, suffix);
					}
					loopvar++;
					brain = new Compute();
					brain.convertJPG(picName);
					tempB = false;
					while (!tempB){
						ArrayList<Integer> pixelList = brain.pixelGet(picName, 0);
						for (int i= 0;i<pixelList.size();i++){
							for(int j = 0;j<areaValues.size();j++){
								//System.out.println(areaValues.get(j));
								if(pixelList.get(i) == (int)areaValues.get(j)){
									tempB = true;
									areaValue  = areaValues.get(j);
									j = areaValues.size();
									i = pixelList.size();
									tempB= true;
								}
							}
						}
						tempB = true;
						//					if(!tempB){
						//						System.out.println("There was an image on which the specified color was not found, the colors found were:\n" + 
						//								pixelList.toString() + "\nplease choose one");
						//						areaValue = k.readInt();
						//						areaValues.add(areaValue);
						//					}
					}
					brain.AreaID(areaValue);
					ArrayList <LPoint> shape = brain.LsetO(areaValue, picName, false);
					shape = brain.Lset(areaValue, picName, shape, false, .1, picName);
					brain.setRemove(shape);
					brain.writepixels(-1, picName);

				}

			}else if (pS == 4){
				System.out.println("Please enter the X coordinate of the seed point as an integer value:");
				x = Keyboard.readInt();
				System.out.println("Please enter the Y coordinate of the seed point as an integer value:");
				y = Keyboard.readInt();
				loopvar = 0;
				Compute brain = new Compute();
				root = picName;
				areaValues.add(areaValue);
				while(loopvar<database){
					if(database !=1){
						picName = nextName(root, constant, loopvar, d, suffix);
					}
					loopvar++;
					brain = new Compute();
					brain.convertJPG(picName);
					tempB = false;
					while (!tempB){
						ArrayList<Integer> pixelList = brain.pixelGet(picName, 0);
						for (int i= 0;i<pixelList.size();i++){
							for(int j = 0;j<areaValues.size();j++){
								//System.out.println(areaValues.get(j));
								if(pixelList.get(i) == (int)areaValues.get(j)){
									tempB = true;
									areaValue  = areaValues.get(j);
									j = areaValues.size();
									i = pixelList.size();
									tempB= true;
								}
							}
						}
						tempB = true;
						//					if(!tempB){
						//						System.out.println("There was an image on which the specified color was not found, the colors found were:\n" + 
						//								pixelList.toString() + "\nplease choose one");
						//						areaValue = k.readInt();
						//						areaValues.add(areaValue);
						//					}
					}
					//brain.AreaID(areaValue);
					ArrayList <Point> shape = brain.expandToFill(x,y);
					//shape = brain.Lset(areaValue, picName, shape);
					brain.PsetRemove(shape);
					brain.writepixels(-1, picName);

				}

			}
			//simple do while loop here, it'll remove the points flagged as bad points, but since the ArrayList is empty the first time
			//around it won't subtract anything until the second loop, and it just repeats until there are no more points that are flagged
			//as bad points.
			System.out.println("done");

		}
	}




	public static int dbID(String s, boolean b, int n, String suffix){
		@SuppressWarnings("unused")
		BufferedImage img = null;
		boolean exists = true;
		int numOn = 1;
		while(exists){
			String name = "dumb";
			if(b){
				String temp = "00000000" + (((Integer)(numOn)).toString());
				name = s + temp.substring(temp.length() - n , temp.length()) + suffix;
			}else{
				name = s + (((Integer)(numOn)).toString()) + suffix;
			}
			try {
				img = ImageIO.read(new File(name));
			}catch (IOException e) {
				exists = false;
			}
			numOn++;
		}
		return numOn-1;

	}
	public static String nextName(String s, boolean b, int n, int d, String suffix){
		if(suffix.equals("unspecified")){
			return s;
		}
		if(b){
			String temp = "0000000" + (((Integer)(n)).toString());
			//System.out.println(temp + " " + n + " " + d +  "\n" + s + temp.substring((temp.length() - d ), temp.length()) + suffix);
			return s + temp.substring(temp.length() - d , temp.length()) + suffix;
		}else{
			return s + (((Integer)(n)).toString()) + suffix;
		}

	}
	public static String generateSettings(){
		String output = "";
		output += "ImageProgramDataStart" +
				"\nCOMMON VARIABLES" +
				"\nDatabase = true" +
				"\nIMG name = \"/users/*EXAMPLE USER*/*FOLDER SYSTEM*/*IMAGE NAME ROOT*.\"" +
				"\nconstant = true" +
				"\ndigits = 4" +
				"\nsuffix = \".png\"" +
				"\nareaValue = -16711423" +
				"\nconvertValue = -1" +
				"\nPROGRAM 1 SPECIFIC" +
				"\ncheckDist = 10" +
				"\nangleMeasure = 2.6111" +
				"\nPROGRAM 2 SPECIFIC" + 
				"\nstep = 0.01" +
				"\nPROGRAM 3 SPECIFIC" +
				"\nresolution = .2" +
				"\nring = false"+
				"\nPROGRAM 4 SPECIFIC" +
				"\nseedX = 100" +
				"\nseedY = 100" +
				"\nPROGRAM 5 SPECIFIC" +
				"\nOPTIONS" +
				"\noption1 = false" +
				"\noption2 = false" +
				"\noption3 = false" +
				"\n\n" +
				"\nPROGRAM SELECT CODE" +
				"\ncode = 3" +
				"\n\n" +
				"\nDIFFERENT WRITE LOCATION" +
				"\nwriteDifferent = false" +
				"\nwritePath = \"/users/*EXAMPLE USER*/*FOLDER SYSTEM*/*IMAGE NAME ROOT*.\"" +
				"\n\n\n" +
				"(Creator comment = the variables above are named what they appear in code, below is an explanation of their meaning" + 
				"\nDatabase - this is a true/false value which tells the program wether or not to look for a database, which is a series of images in a single folder whose names are incremented with some numerical system, which the next variables deal with." +
				"\nIMG name - this is the name and file path of the image(single) or the root for databases, it is important for databases to leave off the numbers!" + 
				"\nconstant - this is another true/false variable, but this one should be marked as true if the database has a constant number of digits"+
				"\ndigits - this is just an integer value that is equal to the number of digits"+
				"\nsuffix - this is a text variable, it needs to have quotation marks and a period in front of it, it is the suffix that will be appended to the image as it is written"+
				"\nareaValue - this is a value of some nature that is a form of threshold, the program has a method that can detect values for this, in order to force the program into running that code, set option1 to true"+
				"\nconvertValue - this is the value that pixels will be given when they're written"+
				"\nchecklist - this is a specific variable for the edge smoothing program, it is the number of pixels, forward and backwards, that the program examines"+
				"\nangleMeasure - this is another specific variable for the due smoothing program, but this one is the minimum angle measure that the program will accept before removing the point it is examining, it is given in radians"+
				"\nstep - this is another program specific variable, but this one deals with how much the ellipse is being shifted per iteration, small values are better but slower"+
				"\nresolution is the size of the check grid used in program 3, small values are slower, but produce better results, large values are fast, but not as effective (this is a square function so halving the values quadruples computing time)" +
				"\nseedX - this is the x coordinate for the seed point that is used in program 4"+
				"\nseedY - this is the y coordinate for the seed point that is used in program 4"+
				"\noption1 - if this is true the program will auto detect an areaValue, which works well but may mis select sometimes"+
				"\noption2 - this variable displays basic debug info, mainly for file reading " +
				"\noption3 - this is stops the program from using the other entered text values and enter a manual data entry mode";
		return output;
	}
	public static ArrayList<String> generateSettingsAL(){
		ArrayList<String> output = new ArrayList<String>();
		output.add( "ImageProgramDataStart");
		output.add(	"COMMON VARIABLES" );
		output.add("Database = true" );
		output.add("IMG name = \"/users/*EXAMPLE USER*/*FOLDER SYSTEM*/*IMAGE NAME ROOT*.\"");
		output.add("constant = true" );
		output.add("digits = 4" );
		output.add("suffix = \".png\"" );
		output.add("areaValue = -16711423" );
		output.add("convertValue = -1" );
		output.add("PROGRAM 1 SPECIFIC" );
		output.add("checkDist = 10" );
		output.add("angleMeasure = 2.6111" );
		output.add("PROGRAM 2 SPECIFIC" );
		output.add("step = 0.01" );
		output.add("PROGRAM 3 SPECIFIC" );
		output.add("resolution = .2" );
		output.add("ring = false");
		output.add("PROGRAM 4 SPECIFIC" );
		output.add("seedX = 100" );
		output.add("seedY = 100" );
		output.add("PROGRAM 5 SPECIFIC" );
		output.add("OPTIONS" );
		output.add("option1 = false" );
		output.add("option2 = false" );
		output.add("option3 = false" );
		output.add("");
		output.add("");
		output.add("");
		output.add("PROGRAM SELECT CODE" );
		output.add("code = 3" );
		output.add("");
		output.add("");
		output.add("");
		output.add("DIFFERENT WRITE LOCATION" );
		output.add("writeDifferent = false" );
		output.add("writePath = \"/users/*EXAMPLE USER*/*FOLDER SYSTEM*/*IMAGE NAME ROOT*.\"" );
		output.add("");
		output.add("");
		output.add("");
		output.add("(Creator comment = the variables above are named what they appear in code, below is an explanation of their meaning" );
		output.add("Database - this is a true/false value which tells the program wether or not to look for a database, which is a series of images in a single folder whose names are incremented with some numerical system, which the next variables deal with." );
		output.add("IMG name - this is the name and file path of the image(single) or the root for databases, it is important for databases to leave off the numbers!" );
		output.add("constant - this is another true/false variable, but this one should be marked as true if the database has a constant number of digits");
		output.add("digits - this is just an integer value that is equal to the number of digits");
		output.add("suffix - this is a text variable, it needs to have quotation marks and a period in front of it, it is the suffix that will be appended to the image as it is written");
		output.add("areaValue - this is a value of some nature that is a form of threshold, the program has a method that can detect values for this, in order to force the program into running that code, set option1 to true");
		output.add("convertValue - this is the value that pixels will be given when they're written");
		output.add("checklist - this is a specific variable for the edge smoothing program, it is the number of pixels, forward and backwards, that the program examines");
		output.add("angleMeasure - this is another specific variable for the due smoothing program, but this one is the minimum angle measure that the program will accept before removing the point it is examining, it is given in radians");
		output.add("step - this is another program specific variable, but this one deals with how much the ellipse is being shifted per iteration, small values are better but slower");
		output.add("resolution is the size of the check grid used in program 3, small values are slower, but produce better results, large values are fast, but not as effective (this is a square function so halving the values quadruples computing time)" );
		output.add("seedX - this is the x coordinate for the seed point that is used in program 4");
		output.add("seedY - this is the y coordinate for the seed point that is used in program 4");
		output.add("option1 - if this is true the program will auto detect an areaValue, which works well but may mis select sometimes");
		output.add("option2 - this variable displays basic debug info, mainly for file reading " );
		output.add("option3 - this is stops the program from using the other entered text values and enter a manual data entry mode");
		return output;
	}
	public static ArrayList<String> generatePaths(){

		ArrayList<String> output = new ArrayList<String>();
		output.add("/Users/samuelweiss/IMGPRGMconfig.txt");
		output.add("/Users/Shared/IMGPRGMconfig.txt");
		output.add("/Users/IMGPRGMconfig.txt");
		output.add("/home/IMGPRGMconfig.txt");
		output.add("c:/Documents and Settings/IMGPRGMconfig.txt");
		output.add("c:/IMGPRGMconfig.txt");
		return output;
	}
}
//int r = (int)((Math.pow(256,3)+rgbs[k]) / 65536); //where rgbs is an array of integers, every single integer represents the RGB values combined in some way
//int g = (int) (((Math.pow(256,3)+rgbs[k]) / 256 ) % 256 );
//int b = (int) ((Math.pow(256,3)+rgbs[k])%256);
//So here is some fun stuff, reading in a text document at some one of the listed options, the document it reads in contains the settings
//		FileRead fr = new FileRead();
//		ArrayList<String> locations = new ArrayList<String>();
//		//possible settings save locations going here, might use this outside of just testing
//		locations.add("/Users/IMGPRGMconfig.txt");
//		locations.add("/home/IMGPRGMconfig.txt");
//		locations.add("c:/IMGPRGMconfig.txt");
//		locations.add("/Users/samuelweiss/IMGPRGMconfig.txt");
//		String path = generateSettings();
//		fr.writeFile(locations, path);
////		locations = new ArrayList<String>();
//		locations = fr.readFile(locations);
//		for(int i = 0; i<locations.size(); i ++){
//			System.out.println(locations.get(i));
//		}