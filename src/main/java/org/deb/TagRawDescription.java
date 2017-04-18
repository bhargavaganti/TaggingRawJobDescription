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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.cloud.language.spi.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.PartOfSpeech.Tag;
import com.google.cloud.language.v1.Token;

/**
 * @author debmalyajash
 *
 */
public class TagRawDescription {

	static int trainingLineCount = 0;

	static int testLineCount = 0;

	static int withoutTag = 0;

	static Set<String> criteriaSet = new HashSet<>();

	static List<Integer> lineNoWithoutTag = new ArrayList<>();

	private static final Logger LOGGER = Logger.getLogger(TagRawDescription.class);

	String[] removalList = new String[] { "  ", "the", "company" };

	String[] keyIngredients = new String[] { "years of experience", "associate", "supervise", "ms or phd", "ms-or-phd",
			"supervising", "hourly", "salary", "part time", "part-time", "licence", "full time", "full-time",
			"bs-degree", "bs degree", "college degree" };

	String[] description = new String[] { "years-experience-needed", "associate-needed", "supervising-job",
			"ms-or-phd-needed", "ms-or-phd-needed", "supervising-job", "hourly-wage", "salary", "part-time-job",
			"part-time-job", "licence-needed", "full-time-job", "full-time-job", "bs-degree-needed", "bs-degree-needed",
			"bs-degree-needed" };

	// Key is tag, value is job description
	Map<String, String> tagJobDescriptionMap = new HashMap<>();

	// Instantiates a client
	LanguageServiceClient language;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			TagRawDescription tag = new TagRawDescription();
			tag.train(args[0]);
			tag.tag(args[1]);
		} else {
			System.err.println("Usage : java org.deb.TagRawDescription <trainging file> <test file>");
		}

	}

	public TagRawDescription() throws IOException {
		language = LanguageServiceClient.create();
	}

	/**
	 * @param testFileName
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void tag(String testFileName) throws IOException {
		PrintWriter writer = null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(testFileName))) {
			writer = new PrintWriter("tags.tsv");
			writer.println("tags");
			String eachLine = bufferedReader.readLine();
			boolean firstLine = true;
			StringBuilder tag = new StringBuilder();
			while (eachLine != null) {
				testLineCount++;

				eachLine = eachLine.toLowerCase();
				if (!firstLine) {
					if (eachLine == null || eachLine.trim().length() == 0) {

					} else {
						String jobDescription = tagJobDescriptionMap.get(eachLine);
						if (jobDescription != null) {
							tag.append(jobDescription);
						} else {
							for (int i = 0; i < keyIngredients.length; i++) {
								if (eachLine.contains(keyIngredients[i])) {
									if (tag.length() > 0) {
										tag.append(" ");
									}
									if (i == 0) {
										// find years of experience required
										int index = eachLine.indexOf(keyIngredients[i]);
										String[] yearFinder = eachLine.substring(0, index).split(" ");
										if (yearFinder[yearFinder.length - 1].equals("1")) {
											tag.append("1-year-experience-needed");
										} else if (yearFinder[yearFinder.length - 1].contains("2")
												|| yearFinder[yearFinder.length - 1].contains("3")
												|| yearFinder[yearFinder.length - 1].contains("4")
												|| yearFinder[yearFinder.length - 1].contains("2-4")) {
											tag.append("2-4-years-experience-needed");
										} else {
											tag.append("5-plus-years-experience-needed");
										}

									} else {
										tag.append(description[i]);
									}

								}
							}
						}

						if (tag.length() == 0) {
							// if not able to tag to any job description
						}
					}
					writer.println(tag.toString());
					tag.delete(0, tag.length());
				} else {
					firstLine = false;
				}
				eachLine = bufferedReader.readLine();
			}
		} catch (Throwable th) {

		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

	}

	/**
	 * @param trainingFileName
	 * @throws IOException
	 */
	public void train(String trainingFileName) throws IOException {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(trainingFileName))) {

			String eachLine = bufferedReader.readLine();
			boolean firstLine = true;
			while (eachLine != null) {
				if (!firstLine) {
					String fields[] = eachLine.split(",");

					if (fields[0].trim().length() == 0) {
						lineNoWithoutTag.add(trainingLineCount);
						withoutTag++;
						// LOGGER.error(trainingLineCount + ") " + eachLine);
					} else {
						String[] mainCrieteria = fields[0].split(" ");
						tagJobDescriptionMap.put(fields[1], fields[0]);
						for (String eachCriteria : mainCrieteria) {
							criteriaSet.add(eachCriteria);
						}
					}
				} else {
					firstLine = false;
				}
				trainingLineCount++;
				eachLine = bufferedReader.readLine();
			}
		} catch (Throwable th) {

		} finally {
			if (withoutTag > 0) {
				LOGGER.error("Without tag count :" + withoutTag);
				// LOGGER.error("Line nos. :" + lineNoWithoutTag);
			}
			LOGGER.debug(criteriaSet);
		}

	}

	/**
	 * 
	 * @param jobDescription
	 * @return
	 */
	public List<String> getKeyWords(final String jobDescription) {
		List<String> keyWordList = new ArrayList<>();
		Document doc = Document.newBuilder().setContent(jobDescription).setType(Type.PLAIN_TEXT).build();
		EncodingType encodingType = EncodingType.NONE;

		AnalyzeEntitiesResponse entities = language.analyzeEntities(doc, encodingType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(entities);
		}
		AnalyzeSyntaxResponse response = language.analyzeSyntax(doc, encodingType);

		List<Token> tokenList = response.getTokensList();
		for (Token eachToken : tokenList) {
			Tag eachTag = eachToken.getPartOfSpeech().getTag();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(eachToken.toString());
			}
			if (eachTag == Tag.NOUN || eachTag == Tag.ADJ || eachTag == Tag.VERB) {
				keyWordList.add(eachToken.getLemma());
			}
		}

		for (String eachIngredient : keyIngredients) {
			if (jobDescription.contains(eachIngredient)) {
				keyWordList.add(eachIngredient);
			}
		}

		return keyWordList;
	}

}

// Reference :
// https://cloud.google.com/natural-language/docs/reference/libraries#installing_the_client_library
// https://cloud.google.com/natural-language/
