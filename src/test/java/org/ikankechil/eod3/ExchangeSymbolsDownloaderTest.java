/**
 * ExchangeSymbolsDownloaderTest.java v0.4 7 April 2015 3:51:55 PM
 *
 * Copyright � 2015-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3;

import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ikankechil.eod3.ExchangeSymbolsDownloader.SymbolsTaskHelper;
import org.ikankechil.eod3.ExchangeSymbolsDownloader.SymbolsTransform;
import org.ikankechil.eod3.sources.Exchanges;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>ExchangeSymbolsDownloader</code>.
 * <p>
 *
 * @author Daniel Kuan
 * @version 0.4
 */
public class ExchangeSymbolsDownloaderTest { // TODO v0.4 Test symbols transform

  public static final File                       DIRECTORY             = new File(".//./tst/" + ExchangeSymbolsDownloaderTest.class.getName().replace('.', '/'));
  public static final File                       SYMBOLS_FILE          = new File(DIRECTORY, "Symbols.csv");
  public static final File                       OHLCV_DIRECTORY       = new File(DIRECTORY, "YahooFinance");

  private static final ExchangeSymbolsDownloader ESD                   = new ExchangeSymbolsDownloader(SYMBOLS_FILE, true);
  private static final SymbolsTaskHelper         SYMBOLS_TASK_HELPER   = ESD.new SymbolsTaskHelper();

  private static final Map<String, Set<String>>  MARKETS               = new LinkedHashMap<>();
  private static final Exchanges[]               UNSUPPORTED_EXCHANGES = { GPW, OSLO, SSE, SZSE, FX };
  private static final String[]                  EXCHANGE_URLS         = { "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=NYSE",
                                                                           "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=NASDAQ",
                                                                           "http://www.nasdaq.com/screening/companies-by-name.aspx?render=download&exchange=AMEX",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/NYSEARCA.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/TSX.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/LON.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/FRA.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/BIT.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/STO.csv",
                                                                           "http://www.netfonds.no/quotes/kurs.php?exchange=CPH&sec_types=&ticks=&table=tab&sort=alphabetic",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/SI.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/HK.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TYO.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Yahoo/NS.csv",
                                                                           "http://s3.amazonaws.com/quandl-static-content/Ticker+CSV's/Google/TPE.csv",
                                                                           "http://www.asx.com.au/asx/research/ASXListedCompanies.csv"
                                                                         };

  private static final String                    EMPTY                 = "";
  private static final String                    SPACE                 = " ";
  private static final char                      COMMA                 = ',';
  private static final Pattern                   PUNCTUATION           = Pattern.compile("\\p{Punct}");

  @Rule
  public ExpectedException                       thrown                = ExpectedException.none();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    if (SYMBOLS_FILE.exists()) {
      SYMBOLS_FILE.setReadable(true);
      SYMBOLS_FILE.setWritable(true);
    }

    MARKETS.put(NYSE.toString(),
                new TreeSet<>(Arrays.asList("123",
                                            "A", "AA", "AAC", "AB", "ABB",
                                            "B", "BAC", "BAK", "BG", "BH",
                                            "C",
                                            "DD",
                                            "M",
                                            "W",
                                            "ZF", "ZTR", "ZX")));
    MARKETS.put(NASDAQ.toString(),
                new TreeSet<>(Arrays.asList("CSCO", "INTC")));


  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    ESD.stop();
  }

  @Before
  public void setUp() throws Exception {
    if (!SYMBOLS_FILE.canWrite()) {
      SYMBOLS_FILE.setWritable(true);
    }
  }

  @Test
  public void cannotInstantiateWithNullFile() {
    thrown.expect(NullPointerException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(null);
  }

  @Test
  public void cannotInstantiateWithNullFile2() {
    thrown.expect(NullPointerException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(null, true);
  }

  @Test
  public void cannotInstantiateWithEmptyFile() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(new File(EMPTY));
  }

  @Test
  public void cannotInstantiateWithWhitespaceFile() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(new File(SPACE));
  }

  @Test
  public void cannotInstantiateWithUnwriteableFile() {
    final File unwriteable = SYMBOLS_FILE;
    unwriteable.setWritable(false);

    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(unwriteable);
  }

  @Test
  public void cannotInstantiateWithDirectory() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(new File(".//./tst/"));
  }

  @Test
  public void cannotDownloadNullExchange() throws Exception {
    thrown.expect(NullPointerException.class);
    final Exchanges[] exchanges = null;
    ESD.download(exchanges);
  }

  @Test
  public void cannotDownloadNullExchange2() throws Exception {
    thrown.expect(NullPointerException.class);
    final String[] exchanges = null;
    ESD.download(exchanges);
  }

  @Test
  public void cannotDownloadEmptyExchange() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.download(new Exchanges[] { /* empty */ });
  }

  @Test
  public void cannotDownloadInvalidExchange() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.download(EMPTY);
  }

  @Test
  public void cannotDownloadUnsupportedExchanges() throws Exception {
    final Map<String, Set<String>> unsupportedExchanges = ESD.download(UNSUPPORTED_EXCHANGES);
    assertTrue(unsupportedExchanges.toString(), unsupportedExchanges.isEmpty());
  }

  @Test
  public void cannotExtractZerothColumn() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final SymbolsTransform transform = new SymbolsTransform(COMMA, 0);
  }

  @Test
  public void cannotExtractNegativeColumn() {
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("unused")
    final SymbolsTransform transform = new SymbolsTransform(COMMA, -1);
  }

  @Test
  public void exchangeURLs() throws Exception {
    final List<URL> actuals = new ArrayList<>(ExchangeSymbolsDownloader.urls().values());
    for (int i = 0; i < EXCHANGE_URLS.length; ++i) {
      // convert URLs to Strings for comparison
      assertEquals("Index: " + i, EXCHANGE_URLS[i], actuals.get(i).toString());
    }
    assertEquals(EXCHANGE_URLS.length, actuals.size());
  }

  @Test
  public void downloadExchanges() throws Exception {
    final Map<String, Set<String>> expecteds = ESD.download(SGX, NYSE);
    final String sgx = SGX.toString();
    final String nyse = NYSE.toString();
    final Map<String, Set<String>> actuals = ESD.download(sgx, nyse);

    assertTrue(actuals.containsKey(sgx));
    assertTrue(actuals.containsKey(nyse));
    assertEquals(expecteds, actuals);
  }

  @Test
  public void downloadRFC2396NonCompliantSymbols() throws Exception {
    // allows non-compliant symbols by default
    final ExchangeSymbolsDownloader esd = new ExchangeSymbolsDownloader(SYMBOLS_FILE);
    final Map<String, Set<String>> actuals = esd.download(new Exchanges[] { KFB });

    for (final Entry<String, Set<String>> market : actuals.entrySet()) {
      final Set<String> symbols = market.getValue();
      int nonCompliantSymbolCount = 0;
      for (final String symbol : symbols) {
        final Matcher matcher = PUNCTUATION.matcher(symbol);
        if (matcher.find()) {
          ++nonCompliantSymbolCount;
        }
      }

      assertTrue(market.getKey(), nonCompliantSymbolCount > 0);
    }
  }

  @Test
  public void cannotCollateNullDirectory() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(null);
  }

  @Test
  public void cannotCollateNullDirectory2() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(null, NYSE);
  }

  @Test
  public void cannotCollateFile() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.collate(SYMBOLS_FILE);
  }

  @Test
  public void cannotCollateFile2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    ESD.collate(SYMBOLS_FILE, NYSE);
  }

  @Test
  public void cannotCollateNullExchange() throws Exception {
    thrown.expect(NullPointerException.class);
    final Exchanges[] exchanges = null;
    ESD.collate(OHLCV_DIRECTORY, exchanges);
  }

  @Test
  public void cannotCollateNullExchange2() throws Exception {
    thrown.expect(NullPointerException.class);
    ESD.collate(OHLCV_DIRECTORY, null, null);
  }

  @Test
  public void cannotCollateNonexistentExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, FX);
    assertTrue(actuals.toString(), actuals.isEmpty());
  }

  @Test
  public void cannotCollateEmptyExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, SGX);
    assertTrue(actuals.toString(), actuals.isEmpty());
  }

  @Test
  public void collateSymbols() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY, NYSE, NASDAQ);

    assertEquals(MARKETS, actuals);
  }

  @Test
  public void collateSymbolsFromAllExchanges() throws Exception {
    final Map<String, Set<String>> actuals = ESD.collate(OHLCV_DIRECTORY);

    assertEquals(MARKETS, actuals);
  }

  @Test
  public void handleExecutionFailure() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleExecutionFailure(new ExecutionException(null),
                                                            AMEX));
  }

  @Test
  public void handleTaskCancellation() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleTaskCancellation(new CancellationException(),
                                                            AMEX));
  }

  @Test
  public void handleTimeout() throws Exception {
    assertEquals(Collections.emptyList(),
                 SYMBOLS_TASK_HELPER.handleTimeout(new TimeoutException(),
                                                   AMEX));
  }

}
