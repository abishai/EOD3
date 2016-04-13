/**
 * QuoteMedia.java	v0.6	24 December 2013 1:40:26 AM
 *
 * Copyright � 2013-2016 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static java.util.Calendar.*;
import static org.ikankechil.eod3.sources.Exchanges.*;
import static org.ikankechil.util.StringUtility.*;

import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;
import org.ikankechil.io.TextTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>Source</code> representing QuoteMedia.
 *
 * @author Daniel Kuan
 * @version 0.6
 */
public class QuoteMedia extends Source {
// TODO cannot seem to narrow download window!

  private static final String SYMBOL      = "&symbol=";

  // Date-related URL parameters
  private static final String START_DATE  = "&startDay=";
  private static final String START_MONTH = "&startMonth=";
  private static final String START_YEAR  = "&startYear=";
  private static final String END_DATE    = "&endDay=";
  private static final String END_MONTH   = "&endMonth=";
  private static final String END_YEAR    = "&endYear=";
  private static final String MAX_YEARS   = "&maxDownloadYears=" + Short.MAX_VALUE;

  // Exchange-related constants
  private static final String LN          = ":LN";
  private static final String SI          = ":SI";
  private static final String AU          = ":AU";
  private static final String CA          = ":CA";
  private static final String FF          = ":FF";
  private static final String HK          = ":HK";
  private static final String SH          = ":SH";
  private static final String SZ          = ":SZ";
  private static final String TK          = ":TK";
  private static final String MB          = ":MB";
  private static final String IN          = ":IN";
  private static final String KR          = ":KR";
  private static final String TW          = ":TW";
  private static final String OS          = ":OS";
  private static final String ST          = ":ST";
  private static final String CO          = ":CO";
  private static final String MI          = ":MI";
  private static final String PA          = ":PA";
  private static final String AS          = ":AS";
  private static final String SM          = ":SM";
  private static final String BV          = ":BV";
  private static final String AR          = ":AR";
  private static final String MX          = ":MX";

  private static final Logger logger      = LoggerFactory.getLogger(QuoteMedia.class);

  public QuoteMedia() {
    this(500); // other valid webmaster IDs: 501, 96483 (american association of individual investors)
  }

  public QuoteMedia(final int webmasterID) {
    super("http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=" + webmasterID + SYMBOL);

    // supported markets (see http://www.quotemedia.com/legal/tos/#times and http://www.quotemedia.com/quotetools/symbolHelp/SymbolHelp_US_Version_Default.html)
    // NYSE, NASDAQ, AMEX and NYSEARCA do not require suffices
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(NYSEARCA, EMPTY);
    exchanges.put(TSX, CA);
    exchanges.put(LSE, LN);
    exchanges.put(FWB, FF);
    exchanges.put(PAR, PA);
    exchanges.put(AMS, AS);
    exchanges.put(SWX, SM);
    exchanges.put(MIB, MI);
    exchanges.put(OSLO, OS);
    exchanges.put(SB, ST);
    exchanges.put(KFB, CO);
    exchanges.put(SGX, SI);
    exchanges.put(HKSE, HK);
    exchanges.put(SSE, SH);
    exchanges.put(SZSE, SZ);
    exchanges.put(TSE, TK);
    exchanges.put(BSE, MB);
    exchanges.put(NSE, IN);
    exchanges.put(KRX, KR);
    exchanges.put(TWSE, TW);
    exchanges.put(ASX, AU);
    exchanges.put(BOVESPA, BV);
    exchanges.put(BCBA, AR);
    exchanges.put(BMV, MX);
    exchanges.put(FX, EMPTY);

    // QuoteMedia
    // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=500
    //                                                             &symbol=<Stock Symbol>
    //                                                             &startDay=02
    //                                                             &startMonth=02
    //                                                             &startYear=2002
    //                                                             &endDay=02
    //                                                             &endMonth=07
    //                                                             &endYear=2009
    //                                                             &isRanged=false
    //                                                             &maxDownloadYears=1000
    // e.g.
    // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=500&symbol=IBM&maxDownloadYears=1000
    // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=501&symbol=INTC&startDay=02&startMonth=02&startYear=2002&endDay=02&endMonth=07&endYear=2009&isRanged=false
    // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=500&symbol=barc:ln
    // http://app.quotemedia.com/quotetools/getHistoryDownload.csv?&webmasterId=501&symbol=$USDSGD
    //
    // http://app.quotemedia.com/quotetools/clientForward?targetURL=http%3A%2F%2Fwww.quotemedia.com%2Ffinance%2Fquote%2F%3Fqm_page%3D43157&targetsym=qm_symbol&targettype=&targetex=&qmpage=true&action=showHistory&symbol=AAPL&startDay=9&startMonth=11&startYear=2010&endDay=9&endMonth=0&endYear=2016
    //
    // Notes:
    // 1. only 3 years' rolling window worth of data if maxDownloadYears parameter not supplied
    // 2. start dates ignored; maxDownloadYears can be used as an offset in conjunction with end dates
    // 3. other valid webmaster IDs: 96483 (american association of individual investors)
  }

  @Override
  void appendSymbolAndExchange(final StringBuilder url,
                               final String symbol,
                               final Exchanges exchange) {
    if (exchange == FX) {
      // prepend currency pair with $ (e.g. $USDJPY)
      url.append(DOLLAR);
      appendSymbol(url, symbol);
    }
    else {
      appendSymbol(url, symbol);
      appendExchange(url, exchange);
    }
  }

  @Override
  void appendStartDate(final StringBuilder url, final Calendar start) {
    url.append(START_DATE).append(start.get(DATE))
       .append(START_MONTH).append(start.get(MONTH) + 1)
       .append(START_YEAR).append(start.get(YEAR));
    logger.debug("Start date: {}", url);
  }

  @Override
  void appendEndDate(final StringBuilder url, final Calendar end) {
    url.append(END_DATE).append(end.get(DATE))
       .append(END_MONTH).append(end.get(MONTH) + 1)
       .append(END_YEAR).append(end.get(YEAR));
    logger.debug("End date: {}", url);
  }

  @Override
  void appendDefaultDates(final StringBuilder url,
                          final Calendar start,
                          final Calendar end) {
    url.append(MAX_YEARS);
    logger.debug("Maximum number of years requested: {}", url);
  }

  @Override
  void appendFrequency(final StringBuilder url, final Frequencies frequency) {
    // do nothing
    logger.debug(UNSUPPORTED);
  }

  @Override
  public TextTransform newTransform(final String symbol) {
    return new TextTransform() {
      @Override
      public String transform(final String line) {
        // QuoteMedia CSV format
        // date,open,high,low,close,volume,changed,changep,adjclose,tradeval,tradevol
        // 2013-12-24,25.38,25.62,25.35,25.43,12157877,0.11,0.43%,25.43,310050677.73,44813

        // MetaStock CSV format
        // Symbol,YYYYMMDD,Open,High,Low,Close,Volume

        final int comma = findNthLast(COMMA, line, FIVE);
        final char[] characters = new char[(symbol.length() + comma) - ONE];
        // set row name
        int i = getChars(symbol, ZERO, symbol.length(), characters, ZERO);
        characters[i] = COMMA;
        // copy years
        i = getChars(line, ZERO, FOUR, characters, ++i);
        // copy month
        i = getChars(line, FIVE, SEVEN, characters, i);
        // copy rest of line
        line.getChars(EIGHT, comma, characters, i);

        return String.valueOf(characters);
      }
    };
  }

}
