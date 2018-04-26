package com.odoo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class Test extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String encoded2 = "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";
        String encoded = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a\n" +
                "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy\n" +
                "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCACAAIADASIA\n" +
                "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\n" +
                "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\n" +
                "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\n" +
                "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\n" +
                "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\n" +
                "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\n" +
                "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\n" +
                "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iii\n" +
                "gAooooAKKKKACiiigAooooAKKKKACiiigAooooA8z/4Xl4Zzj7Dq2f8ArlH/APHKT/heXhg9LLVv\n" +
                "+/Uf/wAcr59keB7tntsgBBkds1E8gihYgfOT+tc6qSZKbPoQfHfwuWYCw1g7ep8mL/45ViX41+G4\n" +
                "VDSWWqrkZwYo/wD45Xz3aW5t2hlmUSI4JCA8sx9abdXHlT7S4Yr1B5GfSn7STegz6Df44eGUiWVr\n" +
                "LVgrdMxR/wDxymr8c/DLttWw1cnGf9TF/wDHK8a0DQk1S0eaW4Vr9WHlRE4UoQD/AI/lUk+mC2uZ\n" +
                "LSWORtSyPIjhUsX9gByaj275uUt0ppXZ69/wvfwy0gjTTdakc9kgjP8A7Uq3/wALj0fGTomugeph\n" +
                "i/8Ajlea6X4E1W3017rWZbXw9bNJu+0X8g8wj/ZjB6/UitI614Z0G1CaXaT+ILtDzc6g3lxKfURg\n" +
                "cj2x+Na+0a3JbSsdvL8ZtChhaVtK1oooyxWGI4/8iVSX49+FWK/6FrA3HHMMf/xyuU14DxL4Ku9T\n" +
                "0qwtrPVLL57+0t1wk0B/5aKPVev0B68V5BDazR3SJIUKk7gynIp891dC6H0rD8a/Dc7lUstVOOSf\n" +
                "Kj4/8fqZfjF4fd1SOw1Z3Y4CrChJ/wDH6+eLc4Wd0fDEY9qsWeu3FrLbvEQk8J4frWMqk+hag31P\n" +
                "pGL4kaNKsX+j36SSnasTxKHz6Y3VrJ4ltnnS3NrdJO4ysbqoYj1+9XEeA9cm8Q2BSXS4PtA4luWK\n" +
                "hW9CB1rb168nt1iuXhjils3Db0+bcDwcfga4Z42snbQ6IYdTtZmq/jGwjcpJb3kbAkYeMDOOveoL\n" +
                "7x1p9latcCyv7hVGSsKIWA/FxU82mpqZg80h7XYXYEYbcemD9M14D4v1a/8AD/j++jsLlnsYWCCK\n" +
                "TlWG0bv1OK1pV69R20MKlNxejPOwsqvnzMbjxjvVq3W7ubiNFjLupyeKzP3owGbaw6ZHFblrqUtv\n" +
                "pM8kcTxYYDzFGQT7ntXbK9tDOV7aFp79IYJN6YuoVKjjg/Ss3TIbi+ukggt5bm8kb91FGhdmPso6\n" +
                "12vg/wCGWveM0jv7tjp2luAVup0y8o/6Zx8Ej/aOBzxmvb/D3hDRPB1i1vo1mQ7jbLcud88/+8/p\n" +
                "7DC+1EadkKKsee+FfAev2+otq2r3NtYlovLFpEgmlA9znYh/Fq6i20e+tLW8j0LUzY3shGbi6iE5\n" +
                "fA6BuNg9cA+w9einOwEOw3Hqq9APr3qtE3lTqy8rn9KzcYp3W5s5ykrNnjHimyuU8QF9cS5tr0Q4\n" +
                "QSSGQTtn7yNyCvTpj6CqOo6RqX2dLs2c8ZCAO4XCsOxzXv2p6Npmv6f9g1S1Se3J3ITw0Tf3kbqr\n" +
                "e4/GuO1W38QeGQ0LQRaxor4VZWAV4x6SDGP+BDg+1RJOOq1NKMYTleT948r0DxDq+ha5a6rFBI0d\n" +
                "tlZUYfLNE33kP1HI9wK39b8DKL9LrRWtR4dv0F1ZTO5yityYtqgt8p/DBAzkGrvjq20+Pwdpl5ZR\n" +
                "La3EjlZ7dSO6nI/AivRtEga00CxitVVIpLaKUx9t5QEsPQnPbA9q0pNTiGKpclmeQTeBp4/3cb3E\n" +
                "hxyYLFmX8y4zTbbwRd2k8V19p8tgc7LuydQfYkMcV7Y0LSsDNGW+q5/rT0i8s5jiZT6qMVq4wZzK\n" +
                "rOOx5tp17rHh+8e7FjbPYFdkr2suQjdsrwR9cY96tx+L5dasJbKS2mdnkDRuiEkjOQDgV3NxY/a7\n" +
                "adLhIxFjeflGfl+btz2HesxZ9dmuLaeO+0aJo4N8JRDtlYjo245UD25rgrYaKacUdVKu3FqTMW/8\n" +
                "aa9a2b4szBHMCkbspyGFea3mhy31ubqLzZptxaZ5HyWJ5PFe+Q+GrjWdD3avMkOoXC5lNq2+NT6p\n" +
                "muE1zw2NE1CeLzpAj4aJum4d81NJSp9LMwrOTd07o8FJSa8Rd+LcsCVJ5A716R8LNG0vX9ekt7uB\n" +
                "zZ2hE7QvICkpz8gYdxwSR3xivKAozwK9f+CFus48Q7h8ytaFWHBH+t6Gu+eiuJK59AtcA5AOT3xV\n" +
                "G5uTj9BiuWj1nULS5e2liSWFMbJdxVyD6jGPxq6upxTcvuU+h5qFXi9B+zZcJz35NGQU2kZGappd\n" +
                "Qu2FlBPpU32mJerCp9omPlaNO2lTaImJB7E/ypZ2529iMc81i3GoqkZMcbytjhVHX86oWl5rc6u1\n" +
                "wYYhkhE27iB7nNQ6q2QuVli+8MeHdXgfSdQtdgut7QyRtjY4Unj04z7dq07CMQ6fawKxYQwpEGPc\n" +
                "KoXP6VwHiy61HR9GvdXa73To0ccJ24Clm54zj7oNdxbSkW8Abq0SHPuVBNVRm2trGlVuUFzO7/4Y\n" +
                "0lIpxZc4HWqfmDoTQZFfkHnp9a35tDlsP1PeNJvFQje9tKq+gJRgK8nHibQY7eJV026kUJGGllf5\n" +
                "mGBu4z9eK9OvSyWN5tPS3kPPrsNeGGxuBpW2FkkmKKwHtgcVnVex04eCle56d4U8XSabqMNjIC2n\n" +
                "3ZZ7eRpGYgbtvc9O/tnFbPxBEU9xpoeZIlJbczHtxXlsllcL4Z0VZpRFM0lygcHG0KEI/lWXe+IZ\n" +
                "bTVmllle7YIADK5bb9KhNc/NK+3T/M1jRuu2p55DBLI4VY2yfavZ/glZzWlr4geYAGSW1C49ll/x\n" +
                "rgY4FUAjtXffDTUFtdRv7KRgouIkkUn+8hIP6P8ApVznoZqB3l0M3YBHJUdPpUAVS3HrVi6fN9IQ\n" +
                "f7ox+AqEYLkAVzdS+gyC1kWdWVsLnmtPywe1Vo2x/Ceo5B7VcU5zx+tOEUtiZyb3I/JB74qZUAWk\n" +
                "J28t+XrSO4AP581WiJOT+IHlv4fhjlBKtdxkgd9qua6K2uvNhjBXBCKP0qjqmkS67EsUCxuLcNO6\n" +
                "u2MjaQMe+aVImMaSQvywU47dKqCla/QJyTSj2NUsA2XPH1xUiyqcBev1zWUt8ofZMmGOOQMVft/L\n" +
                "d8gsO1aJ32Mmg1J2XQdUcHBSynbJ9fLbFeC2rX8EMMsbyPmNdwHTpXvOqSIuh6kshA/0aQHPcFTX\n" +
                "mNjZl4orR2SG4lIRI93OT04pVHZLS5rRbV7Mo3jhfCejmdmVpoZ7plY9AzlV/PaPzrh7xZUnKSIy\n" +
                "N/tDrXpMwsri/mludzWlqIrG3VRkNsAZj9AAn4tXNa/PHq2oh41CxxfKpA61Kk";

        byte[] decodedString = null;


        ImageView imgView = (ImageView) findViewById(R.id.imgProduct);


        try {
            decodedString = Base64.decode(encoded2, Base64.URL_SAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (decodedString != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(
                    decodedString);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            imageStream.reset();
            imgView.setImageBitmap(theImage); //Setting image to the image view
        }
    }
}
