@Grab(group='org.yaml', module='snakeyaml', version='1.23')


import java.io.FileWriter
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions


def data = []

def lines = new File("words.txt")

lines.each { String line ->
    def entry = ['en': line, 'ru': '']
    data.add(entry)
}

def options = new DumperOptions()
options.setPrettyFlow(true)
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

def yaml = new Yaml(options)
def writer = new FileWriter("words.yaml")

yaml.dump(data, writer)


