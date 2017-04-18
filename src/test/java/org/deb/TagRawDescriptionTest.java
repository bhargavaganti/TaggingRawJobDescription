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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author debmalyajash
 *
 */
public class TagRawDescriptionTest {

	/**
	 * Test method for
	 * {@link org.deb.TagRawDescription#main(java.lang.String[])}.
	 */
	@Test
	public final void testMain() {
		try {
			TagRawDescription.main(new String[] { "./src/main/resources/train.csv", "./src/main/resources/test.csv" });
			Assert.assertEquals(4376, TagRawDescription.trainingLineCount);
			Assert.assertEquals(2922, TagRawDescription.testLineCount);

			boolean firstLine = true;
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader("tags.tsv"))) {
				String eachLine = bufferedReader.readLine();
				int lineCount = 0;
				while (eachLine != null) {
					if (!firstLine) {
						String[] tags = eachLine.split(" ");
						for (String eachTag : tags) {
							if (eachTag.trim().length() > 0 && !TagRawDescription.criteriaSet.contains(eachTag)) {
								Assert.assertFalse(lineCount + ") " + eachTag + " unknown tag", true);
							}
						}
					} else {
						firstLine = false;
					}
					lineCount++;
					eachLine = bufferedReader.readLine();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public final void testGetKeyWord() {
		try {
			TagRawDescription trd = new TagRawDescription();
			List<String> keyWordList = trd.getKeyWords(
					"THE COMPANY    Employer is a midstream service provider to the onshore Oil and Gas markets.  "
							+ "It is a a fast growing filtration technology company providing environmentally sound solutions "
							+ "to the E&P’s for water and drilling fluids management and recycling. "
							+ "   THE POSITION    The North Dakota Regional Technical Sales Representative reports directly "
							+ "to the VP of Sales and covers a territory that includes North Dakota and surrounding areas of South Dakota,"
							+ " Wyoming and Montana.  Specific duties for this position include but are not limited to: "
							+ "Building sales volume within the established territory from existing and new accounts Set up and maintain "
							+ "a strategic sales plan for the territory Present technical presentations, "
							+ "product demonstrations & training Maintain direct contact with customers, distributors and representatives "
							+ "Prospect new customer contacts and referrals  "
							+ "Gather and record customer & competitor information   Provide accurate and updated forecasts for the territory"
							+ "   Identify new product opportunities  "
							+ " Build long-term relationships with customers, reps & distributors    CANDIDATE REQUIREMENT    "
							+ "The ideal candidate will possess technical degree, preferably in the oil & gas discipline and/or 5+ years of experience preferably"
							+ " with exploration and production companies (midstream service companies are a big plus).      "
							+ "Other desired requirements include but are not limited to:   "
							+ "  Consistent record of superior sales results & experience closing sales   Proven ability to cold-call, develop relationships "
							+ "  Excellent written and verbal communication skills.    Strong computer skills, including Word, Excel, PowerPoint, e-mail, etc.  "
							+ " Strong work ethic and ability to work independently. "
							+ "  Must be willing to develop new business – not just maintain current accounts  "
							+ " Ability to travel extensively throughout assigned region   "
							+ " If you are a self-motivated individual with strong engineering, and leadership skills and a desire to build a stronger,"
							+ " more advanced organization we encourage you to apply.      "
							+ "Position is located in North Dakota, but sales representative could live as far away as Casper, Wyoming or Billings, Montana. "
							+ "    Successful candidates must pass a post offer background and drug screen.    EOE ");
			Assert.assertNotNull(keyWordList);
			Assert.assertTrue(keyWordList.size() > 1);
		} catch (Throwable th) {
			Assert.assertFalse(th.getMessage(), true);
		}
	}

}
