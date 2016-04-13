/**
 * SourceTest.java	v0.2	7 January 2014 10:06:00 PM
 *
 * Copyright � 2014-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.Frequencies.*;
import static org.ikankechil.eod3.Interval.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.ikankechil.io.TextTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit test for <code>Source</code>.
 *
 * @author Daniel Kuan
 * @version 0.2
 */
public abstract class SourceTest {

  protected Source                       source;

  protected final Map<Exchanges, String> exchanges        = new EnumMap<>(Exchanges.class);
  protected final List<String>           originalLines    = new ArrayList<>();
  protected final List<String>           transformedLines = new ArrayList<>();

  @Rule
  public final ExpectedException         thrown           = ExpectedException.none();

  protected static final String          SYMBOL           = "INTC";
  protected static final Calendar        TODAY            = getInstance();
  protected static final Calendar        YESTERDAY        = (Calendar) TODAY.clone();
  private static final Calendar          FIRST_JANUARY    = getInstance();
  protected static final Calendar        DEFAULT_START    = getInstance();

  private static final String            TEST             = "Test";
  protected static final String          EMPTY            = "";
  protected static final String          SPACE            = " ";
  protected static final char            COLON            = ':';
  protected static final String          DOLLAR           = "$";
  protected static final char            QUESTION         = '?';
  protected static final char            HYPHEN           = '-';

  public static final String             EMPTY_SYMBOL     = "Empty symbol";

  protected static final int             NONE             = -1;

  static { // set dates
    YESTERDAY.add(DAY_OF_MONTH, -1);
    FIRST_JANUARY.set(TODAY.get(YEAR) - 1, JANUARY, 1);
    DEFAULT_START.setTimeInMillis(0);
  }

  @Before
  public void setUp() throws Exception {
    source = newInstance();
  }

  @After
  public void tearDown() throws Exception {
    source = null;
  }

  @Test
  public final void cannotInstantiateWithNullBase() {
    thrown.expect(NullPointerException.class);
    source = newInstance(null);
  }

  @Test
  public final void cannotInstantiateWithEmptyBase() {
    thrown.expect(IllegalArgumentException.class);
    source = newInstance(EMPTY);
  }

  @Test
  public final void cannotInstantiateWithInvalidBase() throws Exception {
    final String invalidProtocol = "invalid://www.test.org";
    source = newInstance(invalidProtocol);

    thrown.expect(MalformedURLException.class);
    source.url(SYMBOL);
  }

  @Test
  public final void cannotCreateURLWithNullSymbol() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null);
  }

  @Test
  public final void cannotCreateURLWithNullSymbol2() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null, SGX);
  }

  @Test
  public final void cannotCreateURLWithNullSymbol3() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithNullSymbol4() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public final void cannotCreateURLWithNullSymbol5() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null, NYSE, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithNullSymbol6() throws Exception {
    thrown.expect(NullPointerException.class);
    source.url(null, FX, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY);
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY, FX);
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol3() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol4() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol5() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY, NYSE, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithEmptySymbol6() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(EMPTY, FX, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE);
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol2() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE, FX);
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol3() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol4() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol5() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE, NYSE, SINCE_INCEPTION.start(), SINCE_INCEPTION.end());
  }

  @Test
  public final void cannotCreateURLWithSpaceSymbol6() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(EMPTY_SYMBOL);
    source.url(SPACE, FX, SINCE_INCEPTION.start(), SINCE_INCEPTION.end(), DAILY);
  }

  @Test
  public void urlWithSymbolOnly() throws Exception {
    final URL expected = expectedURL(SYMBOL);
    final URL actual = source.url(SYMBOL);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolAndExchange() throws Exception {
    final Exchanges exchange = NASDAQ;
    final URL expected = expectedURL(SYMBOL, exchange);
    final URL actual = source.url(SYMBOL, exchange);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolAndFX() throws Exception {
    final Exchanges exchange = FX;
    final URL expected = expectedURL(SYMBOL, exchange);
    final URL actual = source.url(SYMBOL, exchange);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolAndDates() throws Exception {
    final URL expected = expectedURL(SYMBOL, YESTERDAY, TODAY);
    final URL actual = source.url(SYMBOL, YESTERDAY, TODAY);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolAndDates2() throws Exception {
    final URL expected = expectedURL(SYMBOL, FIRST_JANUARY, TODAY);
    final URL actual = source.url(SYMBOL, FIRST_JANUARY, TODAY);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolDatesAndFrequency() throws Exception {
    final URL expected = expectedURL(SYMBOL, YESTERDAY, TODAY, WEEKLY);
    final URL actual = source.url(SYMBOL, YESTERDAY, TODAY, WEEKLY);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolDatesAndExchange() throws Exception {
    final Exchanges exchange = TSX;
    final URL expected = expectedURL(SYMBOL, exchange, YESTERDAY, TODAY);
    final URL actual = source.url(SYMBOL, exchange, YESTERDAY, TODAY);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void urlWithSymbolDatesFrequencyAndExchange() throws Exception {
    final Exchanges exchange = TSX;
    final URL expected = expectedURL(SYMBOL, exchange, YESTERDAY, TODAY, WEEKLY);
    final URL actual = source.url(SYMBOL, exchange, YESTERDAY, TODAY, WEEKLY);

    assertEquals(expected, actual);
    assertNotNull(expected);
    assertNotNull(actual);
  }

  @Test
  public void nullDatesImmaterialForURL() throws Exception {
    final URL expected = source.url(SYMBOL);
    final Exchanges exchange = null;
    final Frequencies frequency = null;

    assertNotNull(expected);
    assertEquals(expected, source.url(SYMBOL, null, null));
    assertEquals(expected, source.url(SYMBOL, null, null, frequency));
    assertEquals(expected, source.url(SYMBOL, exchange, null, null));
    assertEquals(expected, source.url(SYMBOL, exchange, null, null, frequency));
  }

  @Test
  public void nullFrequencyImmaterialForURL() throws Exception {
    final URL expected = source.url(SYMBOL, YESTERDAY, TODAY);

    assertNotNull(expected);
    assertEquals(expected, source.url(SYMBOL, YESTERDAY, TODAY, null));
    assertEquals(expected, source.url(SYMBOL, null, YESTERDAY, TODAY, null));
  }

  @Test
  public void urlsAreRFC2396Compliant() throws Exception {
    final String nonRFC2396 = "^";
    final String url = source.url(nonRFC2396 + SYMBOL).toString();

    assertFalse(url.contains(nonRFC2396));
  }

  @Test
  public void supportedExchanges() throws Exception {
    final Object[] expecteds = exchanges.keySet().toArray();
    final Object[] actuals = source.exchanges().toArray();

    assertArrayEquals(expecteds, actuals);
  }

  @Test
  public void directoryNameIsSourceClassName() {
    final String expected = source.getClass().getSimpleName();
    final String actual = source.directory();

    assertEquals(expected, actual);
    assertNotNull(actual);
  }

  @Test
  public void reader() {
    assertNotNull(source.newReader());
  }

  @Test
  public void transformer() {
    assertNotNull(source.newTransformer(new IdentityTextTransform()));
  }

  @Test
  public void transformLines() throws Exception {
    final TextTransformer xfrmer = source.newTransformer(source.newTransform(SYMBOL));

    final List<String> actuals = xfrmer.transform(new ArrayList<>(originalLines));

    assertArrayEquals(transformedLines.toArray(), actuals.toArray());
  }

  private static final Source newInstance(final String base) {
    return new Source(base) {

      @Override
      protected void appendEndDate(final StringBuilder url, final Calendar end)  { /* do nothing */ }

      @Override
      protected void appendFrequency(final StringBuilder url, final Frequencies frequency) { /* do nothing */ }

      @Override
      protected void appendStartDate(final StringBuilder url, final Calendar start) { /* do nothing */ }

      @Override
      public TextTransform newTransform(final String symbol) {
        return new IdentityTextTransform();
      }

      @Override
      public String directory() {
        return SourceTest.class.getName();
      }

    };
  }

  private Source newInstance()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    return (Source) Class.forName(this.getClass().getName().replace(TEST, EMPTY)).newInstance();
  }

  protected abstract URL expectedURL(final String symbol) throws MalformedURLException;

  protected abstract URL expectedURL(final String symbol,
                                     final Exchanges exchange) throws MalformedURLException;

  protected abstract URL expectedURL(final String symbol,
                                     final Calendar start,
                                     final Calendar end) throws MalformedURLException;

  protected abstract URL expectedURL(final String symbol,
                                     final Calendar start,
                                     final Calendar end,
                                     final Frequencies frequency) throws MalformedURLException;

  protected abstract URL expectedURL(final String symbol,
                                     final Exchanges exchange,
                                     final Calendar start,
                                     final Calendar end) throws MalformedURLException;

  protected abstract URL expectedURL(final String symbol,
                                     final Exchanges exchange,
                                     final Calendar start,
                                     final Calendar end,
                                     final Frequencies frequency) throws MalformedURLException;

  static final class IdentityTextTransform implements TextTransform {
    @Override
    public String transform(final String line) {
      return line;
    }
  }

}