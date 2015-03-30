/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vermeg.convertisseur.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ramzi
 */
@Service("convservice")
public class ConvServiceImpl implements ConvService {

    @Override
    public File load(String id) {
        RestTemplate restTemplate = new RestTemplate();
        String req="http://localhost:8080/archive/document/"+id;
        File file ;
       return( restTemplate.getForObject(req,File.class ));
        
        
    }

    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws InvalidFormatException
     * @throws IOException
     */
    /*this method convert a multipart file to json object */
    @Override
    public JSONObject convert(MultipartFile file)throws FileNotFoundException, InvalidFormatException, IOException {
       
// File file = new File("C:\\Users\\Ramzi\\Documents\\PFE\\developpement\\avancement.xlsx");
    
        File filez=File.createTempFile("fichier", "xslx");
        file.transferTo(filez);
        FileInputStream inp = new FileInputStream(filez);
Workbook workbook = WorkbookFactory.create(inp);

Sheet sheet = workbook.getSheetAt( 0 );

    // Start constructing JSON.
    JSONObject json = new JSONObject();

    // Iterate through the rows.
    JSONArray rows = new JSONArray();
    for ( Iterator<Row> rowsIT = sheet.rowIterator();rowsIT.hasNext(); )
    {
        Row row = rowsIT.next();
        JSONObject jRow = new JSONObject();

        // Iterate through the cells.
        JSONArray cells = new JSONArray();
        for ( Iterator<Cell> cellsIT = row.cellIterator(); cellsIT.hasNext(); )
        {
            Cell cell = cellsIT.next();
           
            if (cell.getCellType()==CELL_TYPE_NUMERIC)
            {
              if (HSSFDateUtil.isCellDateFormatted(cell)) {
        cells.put(cell.getDateCellValue());}
            else
                  cells.put(cell.getNumericCellValue());}
            else
            cells.put( cell.getStringCellValue() );
        }
        jRow.put( "cell", cells );
        rows.put(cells);
        //rows.put( jRow );
    }

    // Create the JSON.
    json.put( "rows", rows );
    System.out.println(json.toString());
    return json;
    }
    
}