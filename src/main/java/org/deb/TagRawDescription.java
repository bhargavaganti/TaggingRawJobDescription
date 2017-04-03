/**
 * Copyright 2015-2016 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

/**
 * @author debmalyajash
 *
 */
public class TagRawDescription {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 1){
			train(args[0]);
			tag(args[1]);
		} else {
			System.err.println("Usage : java org.deb.TagRawDescription <trainging file> <test file>");
		}
		

	}

	/**
	 * @param testFileName
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void tag(String testFileName) throws IOException {
		try (CSVReader reader = new CSVReader(new FileReader(testFileName), '\t')){
			List<String[]> allLines = new ArrayList<>();
			String[] eachLine = reader.readNext();
			while (eachLine != null){
				
				System.out.println("Line :" + eachLine[0]);
				System.out.println("Tag :" + eachLine[1]);
				allLines.add(eachLine);
				eachLine = reader.readNext();
			}
		
			System.out.println("Total number of lines to tag:" + allLines.size());
		} finally {
			
		}
		
	}

	/**
	 * @param trainingFileName
	 * @throws IOException 
	 */
	public static void train(String trainingFileName) throws IOException {
		try (CSVReader reader = new CSVReader(new FileReader(trainingFileName), '\t')){
			List<String[]> allLines = reader.readAll();
			System.out.println("Total number of lines to train:" + allLines.size());
		} finally {
			
		}
		
		
	}

}
