
@Grab('com.squareup.retrofit2:converter-gson:2.4.0') // depends on retrofit
@Grab('org.yaml:snakeyaml:1.23')

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions


def options = new DumperOptions()
options.setPrettyFlow(true)
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

def yaml = new Yaml(options)

def configFile = new File("api_key.yaml")

def config = yaml.load(configFile.text)

def apiKey = config.key


// represents api response
class TranslateResponse {

    List<String> text

}

interface TranslateService {

    @GET("/api/v1.5/tr.json/translate")
    Call<TranslateResponse> getTranslation(
            @Query("key") String apiKey, @Query("text") String text, @Query("lang") String lang)

}

def retrofit = new Retrofit.Builder()
        .baseUrl('https://translate.yandex.net')
        .addConverterFactory(GsonConverterFactory.create())
        .build()

def translateService = retrofit.create(TranslateService.class)


// Fetch translations and collect
// to list of maps

List voc = []

def lines = new File("words.txt")

lines.each { engWord ->

    def call = translateService.getTranslation(apiKey, engWord, "en-ru")

    def resp = call.execute().body()

    // api returns list of possible translations
    // join them
    def ruWord = resp.text.join(", ")

    println(engWord)

    voc.add(['en': engWord, 'ru': ruWord])
}

// write translations to file
def writer = new FileWriter("words.yaml")
yaml.dump(voc, writer)

